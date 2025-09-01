import { useEffect, RefObject } from 'react';
import { useActionStore } from "../../actionStore.tsx"
/**
 * Custom hook to implement scroll snapping behavior to the nearest block.
 * 
 * @param {RefObject<HTMLDivElement>} scrollRef - Reference to the scrollable element
 */
export function useScrollSnapping(scrollRef: RefObject<HTMLDivElement>) {
  const updateCurrentIndex = useActionStore(state => state.updateCurrentIndex);

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;

    let isSnapping = false;
    let timeout: NodeJS.Timeout;

    const snapToNearestBlock = () => {
      if (!el) return; // Safety check

      const blocks = Array.from(el.querySelectorAll('[data-snap-target]')).filter(
        (block): block is HTMLElement => block instanceof HTMLElement
      );

      if (blocks.length === 0) return;

      const centerX = el.scrollLeft + el.clientWidth / 2;

      let closestBlock: HTMLElement | null = null;
      let minDistance = Infinity;

      for (const block of blocks) {
        if (!(block instanceof HTMLElement)) continue;

        const blockCenter = block.offsetLeft + block.offsetWidth / 2;
        const distance = Math.abs(centerX - blockCenter);

        if (distance < minDistance) {
          minDistance = distance;
          closestBlock = block;
        }
      }

      if (closestBlock) {
        const targetScroll = closestBlock.offsetLeft + closestBlock.offsetWidth / 2 - el.clientWidth / 2;
        el.scrollTo({ left: targetScroll, behavior: 'smooth' });

        let i: number = 0;
        for (const block of blocks) {
          if (block === closestBlock) {
            updateCurrentIndex(i);
            console.log("New index: ", i);
          }
          i += 1;
        }
      }
    };

    const onScroll = () => {
      if (!el || isSnapping) return; // Safety check

      clearTimeout(timeout);
      timeout = setTimeout(() => {
        isSnapping = true;
        snapToNearestBlock();
        setTimeout(() => { isSnapping = false; }, 300); // Prevents double-snapping
      }, 100); // Waits 100ms after scroll ends
    };

    el.addEventListener('scroll', onScroll);

    return () => {
      if (el) el.removeEventListener('scroll', onScroll);
      clearTimeout(timeout);
    };
  }, [updateCurrentIndex]);
}

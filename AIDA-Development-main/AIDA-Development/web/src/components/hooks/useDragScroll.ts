import { useEffect, useState } from 'react';
import { useActionStore } from '../../actionStore';


/**
 * Custom hook that enables horizontal drag-to-scroll behavior
 * with momentum / inertial scrolling for a container element.
 *
 * Supports both mouse and touch interactions.
 *
 * @param ref - A React ref pointing to a scrollable HTMLDivElement
 * @param disableScroll - A boolean to control wether the list should scroll or not
 */
export function useDragScroll(ref: React.RefObject<HTMLDivElement>, disableScroll: boolean = false) {
  const stop = useActionStore(state => state.stop);
  
  useEffect(() => {
    const el = ref.current;
    if (!el || disableScroll) return;

    // Internal states for dragging and momentum
    let isDragging = false;
    let startX = 0;
    let scrollLeft = 0;
    let lastX = 0;
    let velocity = 0;
    let animationFrameId: number;

    /** Mouse event handlers **/

    const onMouseDown = (e: MouseEvent) => {
      stop()
      isDragging = true;
      startX = e.pageX - el.offsetLeft;
      scrollLeft = el.scrollLeft;
      lastX = e.pageX;
      el.style.cursor = 'grabbing';
      e.preventDefault(); // Prevent unwanted text selection
    };

    const onMouseMove = (e: MouseEvent) => {
      if (!isDragging) return;
      const x = e.pageX - el.offsetLeft;
      const walk = x - startX;
      el.scrollLeft = scrollLeft - walk;

      // Calculate velocity for momentum
      velocity = e.pageX - lastX;
      lastX = e.pageX;
    };

    const onMouseUp = () => {
      isDragging = false;
      el.style.cursor = 'grab';
      momentumScroll(); // Start inertial scrolling
    };

    /** 
     * Function that continues scrolling after release,
     * using decreasing velocity (friction).
     */
    const momentumScroll = () => {
      if (Math.abs(velocity) < 0.5) return; // Stop if too slow

      el.scrollLeft -= velocity;
      velocity *= 0.80; // Apply friction factor (decay)

      animationFrameId = requestAnimationFrame(momentumScroll);
    };

    /** Attach event listeners **/
    el.addEventListener('mousedown', onMouseDown);
    el.addEventListener('mousemove', onMouseMove);
    el.addEventListener('mouseup', onMouseUp);
    el.addEventListener('mouseleave', onMouseUp);

    /** Cleanup listeners on unmount **/
    return () => {
      el.removeEventListener('mousedown', onMouseDown);
      el.removeEventListener('mousemove', onMouseMove);
      el.removeEventListener('mouseup', onMouseUp);
      el.removeEventListener('mouseleave', onMouseUp);

      cancelAnimationFrame(animationFrameId);
    };
  }, [ref, disableScroll]);
}

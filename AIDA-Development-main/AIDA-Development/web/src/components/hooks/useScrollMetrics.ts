import { useState, useEffect, RefObject } from 'react';

/**
 * Custom hook to track scroll metrics (scrollLeft, scrollWidth, clientWidth) of a scrollable element.
 * 
 * @param {RefObject<HTMLDivElement>} scrollRef - Reference to the scrollable element
 * @returns {Object} An object containing the current scroll metrics
 */
export function useScrollMetrics(scrollRef: RefObject<HTMLDivElement>) {
  // Local state to hold scroll metrics of the container
  const [scrollMetrics, setScrollMetrics] = useState({ scrollLeft: 0, scrollWidth: 0, clientWidth: 0 });

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;

    // Update function to refresh scroll metrics
    const updateScrollMetrics = () => {
      if (!el) return; // Safety check
      const { scrollLeft, scrollWidth, clientWidth } = el;
      setScrollMetrics({ scrollLeft, scrollWidth, clientWidth });
    };

    // Run once on mount
    updateScrollMetrics();

    // Listen for scroll events on the container
    el.addEventListener('scroll', updateScrollMetrics);

    // Use ResizeObserver for container size changes
    let resizeObserver: ResizeObserver;
    try {
      resizeObserver = new ResizeObserver(() => {
        if (el) updateScrollMetrics();
      });
      resizeObserver.observe(el);
    } catch (error) {
      console.error("ResizeObserver error:", error);
    }

    // Use MutationObserver to capture changes in the children that affect scrollWidth
    let mutationObserver: MutationObserver;
    try {
      mutationObserver = new MutationObserver(() => {
        if (el) updateScrollMetrics();
      });
      mutationObserver.observe(el, { childList: true, subtree: true });
    } catch (error) {
      console.error("MutationObserver error:", error);
    }

    // Listen to window resize events
    window.addEventListener('resize', updateScrollMetrics);

    // Cleanup on unmount
    return () => {
      el.removeEventListener('scroll', updateScrollMetrics);
      if (resizeObserver) resizeObserver.disconnect();
      if (mutationObserver) mutationObserver.disconnect();
      window.removeEventListener('resize', updateScrollMetrics);
    };
  }, [scrollRef]);

  return { scrollMetrics };
}

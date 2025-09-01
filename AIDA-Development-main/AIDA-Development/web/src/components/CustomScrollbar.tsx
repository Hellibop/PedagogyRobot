import React, { useRef } from 'react';

/**
 * Props for the CustomScrollbar component.
 *
 * @interface CustomScrollbarProps
 * @property {number} scrollLeft - The current horizontal scroll position of the container.
 * @property {number} scrollWidth - The total scrollable width of the container.
 * @property {number} clientWidth - The visible width of the container.
 * @property {React.RefObject<HTMLDivElement>} scrollContainerRef - A ref to the scrollable container.
 */
interface CustomScrollbarProps {
  scrollLeft: number;
  scrollWidth: number;
  clientWidth: number;
}

/**
 * CustomScrollbar component.
 *
 * This component renders a custom horizontal scrollbar that visually reflects and controls
 * the scrolling of a separate scrollable container. It calculates the thumb (draggable element)
 * width and position based on the scrollable container's metrics and allows dragging (via mouse)
 * to adjust the scroll position.
 *
 * @param {CustomScrollbarProps} props - The props for the component.
 * @returns {JSX.Element | null} The rendered custom scrollbar or null if no scrolling is needed.
 */
const CustomScrollbar: React.FC<CustomScrollbarProps> = ({
  scrollLeft,
  scrollWidth,
  clientWidth,
}) => {

  // If the content fits within the container, no scrollbar is necessary.
  if (scrollWidth <= clientWidth) return null;

  // Refs to the track and thumb elements.
  const trackRef = useRef<HTMLDivElement>(null);
  const thumbRef = useRef<HTMLDivElement>(null);

  // Calculate the visual dimensions for the custom scrollbar.
  // The track width is set to 50% of the window's inner width.
  const trackWidth = window.innerWidth;
  // The thumb width is a ratio of the visible area to the overall scrollable area, with a minimum of 20px.
  const thumbWidth = Math.max((clientWidth / scrollWidth) * trackWidth, 20);
  // The thumb's left position is calculated based on the current scroll position.
  const thumbLeft = (scrollLeft / (scrollWidth - clientWidth)) * (trackWidth - thumbWidth);


  return (
    <div className="w-full opacity-70">
      {/* The scrollbar track */}
      <div
        ref={trackRef}
        className="relative h-2 bg-gray-300 overflow-hidden mx-auto"
        style={{ width: trackWidth }}
      >
        {/* The scrollbar thumb */}
        <div
          ref={thumbRef}
          className="absolute top-0 h-full bg-blue-500 transition-all"
          style={{
            width: thumbWidth,
            left: thumbLeft,
          }}
        />
      </div>
    </div>
  );
};

export default CustomScrollbar;

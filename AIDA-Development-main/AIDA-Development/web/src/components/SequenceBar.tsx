import { useRef, useState, useEffect } from "react";
import CustomScrollbar from "./CustomScrollbar";
import { DndContext, closestCenter, DragEndEvent } from '@dnd-kit/core';
import { SortableContext, horizontalListSortingStrategy } from '@dnd-kit/sortable';
import { useActionStore } from '../actionStore';
import { restrictToHorizontalAxis, restrictToParentElement } from '@dnd-kit/modifiers';
import { SortableItem } from './SortableItem';
import { useScrollSnapping } from './hooks/useScrollSnapping';
import { useScrollMetrics } from './hooks/useScrollMetrics';
import { useDragScroll } from './hooks/useDragScroll';

/**
 * Component: ActionBlockSeqList
 *
 * A horizontally scrollable list of draggable action blocks.
 * 
 * Features:
 * - Horizontal drag-and-drop sorting using @dnd-kit.
 * - Background dragging to scroll (only when not interacting with items).
 * - Smooth scroll snapping.
 * - Custom scrollbar overlay.
 * 
 * @returns {JSX.Element} Rendered ActionBlockSeqList component.
 */
export function ActionBlockSeqList() {
  // Zustand store hooks for actions and state management
  const actions = useActionStore(state => state.actions);
  const moveAction = useActionStore(state => state.moveAction);
  const stop = useActionStore(state => state.stop);
  const setScrollRef = useActionStore(state => state.setScrollRef);

  // Ref to the scrollable container
  const scrollRef = useRef<HTMLDivElement>(null);

  // Tracks whether the user is interacting with a draggable item
  const [isInteractingWithItem, setIsInteractingWithItem] = useState(false);

  // Enables drag-to-scroll behavior unless interacting with an item
  useDragScroll(scrollRef, isInteractingWithItem);

  // Hooks to support snapping and custom scroll metrics
  const { scrollMetrics } = useScrollMetrics(scrollRef);
  useScrollSnapping(scrollRef);

  // Set the scrollRef in Zustand after mount
  useEffect(() => {
    if (scrollRef.current) {
      setScrollRef({ current: scrollRef.current });
    }
  }, [setScrollRef]);

  /**
   * Handles the logic for when a drag operation ends.
   * Updates the order of actions if needed.
   *
   * @param {DragEndEvent} event - Event from dnd-kit.
   */
  const handleDragEnd = (event: DragEndEvent) => {
    stop();
    const { active, over } = event;
    if (over && active.id !== over.id) {
      const oldIndex = actions.findIndex(a => a.uid === active.id);
      const newIndex = actions.findIndex(a => a.uid === over.id);
      moveAction(oldIndex, newIndex);
    }
  };

  return (
    <div className="w-screen relative z-2">
      {/* Scrollable container */}
      <div
        ref={scrollRef}
        className="flex flex-row h-[50vh] items-center bg-transparent w-full overflow-x-auto scrollbar-hidden"
        style={{
          touchAction: 'pan-x', // Allows touch scrolling horizontally
          cursor: 'grab',
        }}
      >
        {/* Left padding */}
        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

        {/* Action blocks area */}
        <div
          className="bg-transparent h-40 flex items-center justify-center gap-2"
          onMouseEnter={() => setIsInteractingWithItem(true)}
          onMouseLeave={() => setIsInteractingWithItem(false)}
        >
          <DndContext
            collisionDetection={closestCenter}
            onDragEnd={handleDragEnd}
            modifiers={[restrictToHorizontalAxis, restrictToParentElement]}
          >
            <SortableContext
              items={actions.map(action => action.uid)}
              strategy={horizontalListSortingStrategy}
            >
              <div className="flex gap-2">
                {actions.map(action => (
                  <SortableItem key={action.uid} action={action} />
                ))}
              </div>
            </SortableContext>
          </DndContext>

          {/* Right-side invisible spacer for layout */}
          <div className="w-40 h-40" />
        </div>

        {/* Right padding */}
        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

        {/* Custom scrollbar overlay */}
        <div className="absolute bottom-0 flex" style={{ zIndex: 10 }}>
          <CustomScrollbar
            scrollLeft={scrollMetrics.scrollLeft}
            scrollWidth={scrollMetrics.scrollWidth}
            clientWidth={scrollMetrics.clientWidth}
          />
        </div>
      </div>
    </div>
  );
}

import { useRef, useState, useEffect } from "react";
import CustomScrollbar from "./CustomScrollbar";

//import { DndContext, closestCenter, DragEndEvent } from '@dnd-kit/core';
import { useDroppable, useDndContext, type DragEndEvent } from '@dnd-kit/core';
import { SortableContext, horizontalListSortingStrategy } from '@dnd-kit/sortable';
import { useActionStore } from '../actionStore';

// (not used here)
// import { restrictToHorizontalAxis, restrictToParentElement } from '@dnd-kit/modifiers';
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
 * - Position badge to display postion in sequence
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

  // Tracks whether the user is interacting with (or pressing in) the action area
  const [isInteractingWithItem, setIsInteractingWithItem] = useState(false);

  // Hooks to support snapping and custom scroll metrics
  const { scrollMetrics } = useScrollMetrics(scrollRef);
  useScrollSnapping(scrollRef);

  // Set the scrollRef in Zustand after mount
  useEffect(() => {
    if (scrollRef.current) {
      setScrollRef({ current: scrollRef.current });
    }
  }, [setScrollRef]);

  // Detect drag state once; reuse it
  const dnd = useDndContext();
  const isDraggingAnywhere = !!dnd.active;
  const isGridDrag = dnd.active?.data?.current?.source === 'grid';

  // Enables drag-to-scroll behavior unless interacting with an item OR any drag is active
  useDragScroll(scrollRef, isInteractingWithItem || isDraggingAnywhere);

  // Background + append droppables (attached only during grid→sequence drags)
  const { setNodeRef: setDropZoneRef, isOver } = useDroppable({ id: 'sequence-bar-drop' });
  const { setNodeRef: setAppendRef, isOver: isOverAppend } = useDroppable({ id: 'sequence-append' });

  /**
   * Handles the logic for when a drag operation ends.
   * Updates the order of actions if needed.
   *
   * NOTE: App.tsx owns DndContext and handles onDragEnd for both grid→sequence and in-sequence reorder.
   * This function is kept only for historical/reference purposes (not used).
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
      <div
        ref={scrollRef}
        className="flex flex-row h-[50vh] items-center bg-transparent w-full overflow-x-auto scrollbar-hidden"
        style={{ touchAction: 'pan-x', cursor: 'grab' }}
      >
        {/* Left padding */}
        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

        {/* Action blocks area */}
        <div
          // ⬇︎ only attach the background droppable when dragging from grid
          ref={isGridDrag ? setDropZoneRef : undefined}
          className={`bg-transparent h-40 flex items-center justify-center gap-2 transition-colors duration-200 ${isGridDrag && isOver ? "bg-blue-100" : ""}`}
          onMouseEnter={() => setIsInteractingWithItem(true)}
          onMouseLeave={() => setIsInteractingWithItem(false)}
          onPointerDown={() => setIsInteractingWithItem(true)}   // ensure scroll is disabled BEFORE drag starts
          onPointerUp={() => setIsInteractingWithItem(false)}    // re-enable after
          onPointerCancel={() => setIsInteractingWithItem(false)}
        >
          <SortableContext
            items={actions.map(action => action.uid)}
            strategy={horizontalListSortingStrategy}
          >
            <div className="flex gap-2">
              {actions.map((action, index) => (
                <SortableItem
                  key={action.uid}
                  action={action}
                  position={index + 1}
                />
              ))}
            </div>
          </SortableContext>

          {/* ⬇︎ append sentinel exists ONLY for grid drags, so it never affects internal reordering */}
          {isGridDrag && (
            <div
              ref={setAppendRef}
              className={`w-40 h-40 ${isOverAppend ? 'ring-2 ring-blue-400 rounded-md' : ''}`}
            />
          )}
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

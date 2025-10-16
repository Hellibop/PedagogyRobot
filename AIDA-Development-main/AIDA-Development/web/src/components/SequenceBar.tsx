import { useRef, useState, useEffect } from "react";
import CustomScrollbar from "./CustomScrollbar";
import { DndContext, closestCenter, DragEndEvent, useDroppable } from '@dnd-kit/core';
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
  // For adding a new action at the end
  const addAction = useActionStore(state => state.addAction);

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

  // Add a drop zone around the actions area for grid-to-sequence drag-in
  const { setNodeRef: setDropZoneRef, isOver: isGridDropOver } = useDroppable({
    id: "sequence-bar-drop",
  });

  /**
   * Handles the logic for when a drag operation ends.
   * Updates the order of actions if needed (inner drag),
   * or adds a new action at the end if the drag comes from the grid.
   * 
   * @param {DragEndEvent} event - Event from dnd-kit.
   */
  const handleDragEnd = ({ active, over }: DragEndEvent) => {
    stop?.();

    const data = active?.data?.current as any;

    //  Dragging in from the grid 
    if (data?.source === 'grid' && data?.action) {
      // Default to append if we didn't drop over a specific item
      let insertIndex = actions.length;

      // If we dropped over a specific sequence item, insert BEFORE that item
      if (over && over.id !== 'sequence-bar-drop') {
        const overIndex = actions.findIndex(a => a.uid === over.id);
        if (overIndex !== -1) {
          insertIndex = overIndex; // insert before the hovered item
        }
      }

      // 1) append to end (the existing behavior)
      const preLength = useActionStore.getState().actions.length;
      addAction(data.action);

      // 2) then move the newly appended item into the target slot
      //    (wait a frame so the appended item exists in state)
      requestAnimationFrame(() => {
        const { actions: latest, moveAction } = useActionStore.getState();
        const newIndex = latest.length - 1; // appended at the end
        if (insertIndex < newIndex) {
          moveAction(newIndex, insertIndex);
        }
      });

      return;
    }

    // Reordering inside the sequence (existing/old logic) 
    if (over && active?.id !== over.id) {
      const from = actions.findIndex(a => a.uid === active.id);
      const to = actions.findIndex(a => a.uid === over.id);
      if (from !== -1 && to !== -1) {
        moveAction(from, to);
      }
    }
  };
  return (
    <div className="w-screen relative z-2">
      <div
        ref={scrollRef}
        className="flex flex-row h-[50vh] items-center bg-transparent w-full overflow-x-auto scrollbar-hidden"
        style={{ touchAction: 'pan-x', cursor: 'grab' }}
      >
        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

        <div
          ref={setDropZoneRef}
          className={`bg-transparent h-40 flex items-center justify-center gap-2 transition-colors duration-200 ${isGridDropOver ? "bg-blue-100" : ""}`}
          onMouseEnter={() => setIsInteractingWithItem(true)}
          onMouseLeave={() => setIsInteractingWithItem(false)}
        >
          {/* No DndContext here anymore */}
          <SortableContext
            items={actions.map(action => action.uid)}
            strategy={horizontalListSortingStrategy}
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
          </DndContext>

          {/* Right-side invisible spacer for layout */}
          <div className="w-40 h-40" />
        </div>

        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

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
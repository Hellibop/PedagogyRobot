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
 */
export function ActionBlockSeqList() {
  const actions = useActionStore(state => state.actions);
  const moveAction = useActionStore(state => state.moveAction);
  const stop = useActionStore(state => state.stop);
  const setScrollRef = useActionStore(state => state.setScrollRef);

  const scrollRef = useRef<HTMLDivElement>(null);
  const [isInteractingWithItem, setIsInteractingWithItem] = useState(false);

  useDragScroll(scrollRef, isInteractingWithItem);
  const { scrollMetrics } = useScrollMetrics(scrollRef);
  useScrollSnapping(scrollRef);

  useEffect(() => {
    if (scrollRef.current) {
      setScrollRef({ current: scrollRef.current });
    }
  }, [setScrollRef]);

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
        <div style={{ minWidth: "calc(50vw - 5rem)" }} />

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
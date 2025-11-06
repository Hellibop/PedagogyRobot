import './App.css';
import React, { useState } from 'react';
import Footer from './components/Footer';
import ClearButton from './components/ClearButton';
import { ActionBlockSeqList } from './components/SequenceBar';
import { useActionStore } from './actionStore';
import { loadActionsFromStorage } from './dataclasses/Loader';
import { QrPopup } from './parser_qr/QrPopup';
import {
  DndContext,
  closestCenter,
  type DragEndEvent,
  useSensor,
  useSensors,
  MouseSensor,
  TouchSensor,
  DragOverlay,
  defaultDropAnimation,
  type DropAnimation,
  type DragStartEvent,
} from '@dnd-kit/core';
import { ActionBlock } from './components/actionblocks/ActionBlock';
import { DynamicActionBlock } from './components/DynamicActionBlock';

// preload actions
useActionStore.getState().actions = loadActionsFromStorage();

/**
 *  Renders Root Component of the Application;
 *  This component serves as the main entry point for the application.
 *  It initializes the state and renders the main layout.
 * 
 * 
 * @returns {JSX.Element} Rendered App component.
 */
function App() {
  const [sequence, setSequence] = useState<React.ReactNode[]>([]);
  const playing = useActionStore((s) => s.playing);
  const actions = useActionStore((s) => s.actions);
  const addAction = useActionStore((s) => s.addAction);
  const moveAction = useActionStore((s) => s.moveAction);
  const stop = useActionStore((s) => s.stop);

  const sensors = useSensors(
    useSensor(MouseSensor, { activationConstraint: { distance: 8 } }),
    useSensor(TouchSensor, { activationConstraint: { delay: 150, tolerance: 5 } }),
  );



  // track the item being dragged
  const [activeDrag, setActiveDrag] = useState<any | null>(null);

  const dropAnimation: DropAnimation = {
    ...defaultDropAnimation,
    duration: 250,
    easing: 'cubic-bezier(0.2, 0, 0, 1)',
  };

  const handleDragStart = (event: DragStartEvent) => {
    setActiveDrag(event.active?.data?.current || null);
  };

  const handleDragEnd = ({ active, over }: DragEndEvent) => {
    stop?.();
    const data = active?.data?.current as any;
    // grid→sequence
    if (data?.source === 'grid' && data?.action) {
      let insertIndex = actions.length;
      if (over && over.id !== 'sequence-bar-drop' && over.id !== 'sequence-append') {
        const overIndex = actions.findIndex((a) => a.uid === over.id);
        if (overIndex !== -1) insertIndex = overIndex;
      }
      // append then move into place
      addAction(data.action);
      requestAnimationFrame(() => {
        const { actions: latest, moveAction } = useActionStore.getState();
        const newIndex = latest.length - 1;
        if (insertIndex < newIndex) moveAction(newIndex, insertIndex);
      });
      return;
    }
    // sequence reordering
    if (over && active?.id !== over.id) {
      const from = actions.findIndex((a) => a.uid === active.id);
      const to = actions.findIndex((a) => a.uid === over.id);
      if (from !== -1 && to !== -1) moveAction(from, to);
    }
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragStart={handleDragStart}
      onDragEnd={(e) => {
        handleDragEnd(e);
        setActiveDrag(null);
      }}
      onDragCancel={() => setActiveDrag(null)}
    >
      <div className={`${playing ? 'bg-gray-400' : 'bg-white'} flex flex-col h-screen`}>
        <div className="flex items-center h-1/2">
          <ActionBlockSeqList />
          <div className="absolute h-1/2 left-1/2 transform -translate-x-1/2 w-40 bg-green-400 z-0 pointer-events-none"></div>
        </div>

        {/* top buttons */}
        <div className="absolute top-3 right-3 z-1000">
          <QrPopup />
        </div>
        <div className="absolute top-3 left-3 z-1000">
          <ClearButton />
        </div>

        {/* drag overlay for both sources */}
        <DragOverlay dropAnimation={dropAnimation}>
          {activeDrag ? (
            activeDrag.source === 'grid' && activeDrag.action ? (
              <div className="h-40 w-40 rounded-xl shadow-xl">
                <ActionBlock action={activeDrag.action} />
              </div>
            ) : activeDrag.source === 'sequence' && activeDrag.action ? (
              <div className="flex flex-col items-center">
                <DynamicActionBlock
                  action={activeDrag.action.action}
                  uid={activeDrag.action.uid}
                />
              </div>
            ) : null
          ) : null}
        </DragOverlay>

        {/* footer including movement and special grids */}
        <div className="h-1/2 w-screen">
          <Footer addBlock={(block) => setSequence([...sequence, block])} />
        </div>
      </div>
    </DndContext>
  );
}

export default App;

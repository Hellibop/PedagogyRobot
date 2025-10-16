import './App.css'
import React from 'react';
import { useState } from 'react';
import Footer from './components/Footer'
import "./App.css";
import ClearButton from './components/ClearButton';

import { ActionBlockSeqList } from './components/SequenceBar';
import { useActionStore } from './actionStore';
import { loadActionsFromStorage } from './dataclasses/Loader';
import { QrPopup } from './parser_qr/QrPopup';
// NEW:
// import { DndContext, closestCenter, type DragEndEvent } from '@dnd-kit/core';
// import {
//   DndContext,
//   closestCenter,
//   type DragEndEvent,
//   useSensor,
//   useSensors,
//   MouseSensor,
//   TouchSensor,
// } from '@dnd-kit/core';
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
import { ActionBlock } from './components/actionblocks/ActionBlock'; // same visual as your grid tile


useActionStore.getState().actions = loadActionsFromStorage()

/**
 *  Renders Root Component of the Application;
 *  This component serves as the main entry point for the application.
 *  It initializes the state and renders the main layout.
 * 
 * 
 * @returns {JSX.Element} Rendered App component.
 */

function App() {
  const [sequence, setSequence] = useState<React.ReactNode[]>([]);

  function addBlock(block: React.ReactNode) {

    setSequence([...sequence, block]);
  }


  const playing = useActionStore(state => state.playing);

  // NEW: selectors from store for drag handling
  //to specify for future developers, the NEW comments are new things which was needed to be able to drag into sequence,
  //whilst existing/old was the behaviour that existed before drag was implemented, if you wish to change so you have only drag or 
  //perhaps make it so drag inside is more "clean" you may want to change the code which has existing/old logic comments as those are the relevant parts
  const actions = useActionStore(s => s.actions);
  const addAction = useActionStore(s => s.addAction);
  const moveAction = useActionStore(s => s.moveAction);
  const stop = useActionStore(s => s.stop);
  //new
  const sensors = useSensors(
    // desktop: require a small move before drag
    useSensor(MouseSensor, { activationConstraint: { distance: 8 } }),
    // touch: require a short press to start drag (prevents taps becoming drags)
    useSensor(TouchSensor, { activationConstraint: { delay: 150, tolerance: 5 } }),
  );
  //new
  const [activeDrag, setActiveDrag] = useState<any | null>(null);

  const dropAnimation: DropAnimation = {
    ...defaultDropAnimation,
    duration: 250,
    easing: 'cubic-bezier(0.2, 0, 0, 1)',
  };

  const handleDragStart = (event: DragStartEvent) => {
    setActiveDrag(event.active?.data?.current || null);
  };
  // NEW: single drag-end handler for the whole workspace
  const handleDragEnd = ({ active, over }: DragEndEvent) => {
    stop?.();

    const data = active?.data?.current as any;

    // inside handleDragEnd in App.tsx
    if (data?.source === 'grid' && data?.action) {
      let insertIndex = actions.length; // default: append

      if (over && over.id !== 'sequence-bar-drop' && over.id !== 'sequence-append') {
        const overIndex = actions.findIndex(a => a.uid === over.id);
        if (overIndex !== -1) {
          insertIndex = overIndex; // insert before hovered item
        }
      }

      // append then move
      addAction(data.action);
      requestAnimationFrame(() => {
        const { actions: latest, moveAction } = useActionStore.getState();
        const newIndex = latest.length - 1; // appended at end
        if (insertIndex < newIndex) moveAction(newIndex, insertIndex);
      });
      return;
    }


    //  Reordering inside the sequence (existing/old logic) 
    if (over && active?.id !== over.id) {
      const from = actions.findIndex(a => a.uid === active.id);
      const to = actions.findIndex(a => a.uid === over.id);
      if (from !== -1 && to !== -1) {
        moveAction(from, to);
      }
    }
  };
  return (
    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragStart={handleDragStart} onDragEnd={(e) => { handleDragEnd(e); setActiveDrag(null); }}
      onDragCancel={() => setActiveDrag(null)}>
      <div className={`${playing ? 'bg-gray-400' : 'bg-white'} flex flex-col h-screen`}>
        <div className='flex items-center h-1/2'>
          <ActionBlockSeqList />
          {/* <div className="absolute h-1/2 left-1/2 transform -translate-x-1/2 w-40 bg-green-400 z-0"></div> */}
          <div className="absolute h-1/2 left-1/2 transform -translate-x-1/2 w-40 bg-green-400 z-0 pointer-events-none"></div>
        </div>

        {/* Export / Clear */}
        <div className="absolute top-3 right-3 z-1000">
          <QrPopup />
        </div>
        <div className="absolute top-3 left-3 z-1000">
          <ClearButton />
        </div>
        <DragOverlay dropAnimation={dropAnimation}>
          {activeDrag?.source === 'grid' && activeDrag?.action ? (
            <div className="h-40 w-40 rounded-xl shadow-xl">
              <ActionBlock action={activeDrag.action} />
            </div>
          ) : null}
        </DragOverlay>
        {/* The grid is likely rendered inside Footer; that's fine—it's inside the same DndContext */}
        <div className='h-1/2 w-screen'>
          <Footer addBlock={addBlock} />
        </div>
      </div>
    </DndContext>
  )
}

window.addEventListener("beforeunload", () => {
  const state = useActionStore.getState();
  localStorage.setItem("action-list", JSON.stringify(state.actions));
});

export default App;
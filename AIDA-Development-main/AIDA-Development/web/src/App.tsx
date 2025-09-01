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

  return (
    <div className={`${playing ? 'bg-gray-400' : 'bg-white'} flex flex-col h-screen`}>
      <div className='flex items-center h-1/2'>
        <ActionBlockSeqList />
        <div className="absolute h-1/2 left-1/2 transform -translate-x-1/2 w-40 bg-green-400 z-0"></div>

      </div >
      {/** Export Button */}
      <div className="absolute top-3 right-3 z-1000">
        <QrPopup />
      </div>
      <div className="absolute top-3 left-3 z-1000">
        <ClearButton />
      </div>
      <div className='absolute top-3 justify-center flex align-center w-screen'>
      </div>
      <div className='h-1/2 w-screen'>
        <Footer addBlock={addBlock} />
      </div>
    </div>
  )
}

window.addEventListener("beforeunload", (event) => {
  const state = useActionStore.getState(); // get the latest state from your Zustand store

  // Save actions to localStorage
  localStorage.setItem("action-list", JSON.stringify(state.actions));

});

export default App;

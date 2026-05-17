import React from 'react';
import { useActionStore } from '../actionStore';
import { ActionBlockSeqList } from '../components/SequenceBar';
import Footer from '../components/Footer';
import { RobotSimulationOverlay } from '../components/RobotSimulationOverlay';
import ClearButton from '../components/ClearButton';

export default function ChallengePlay() {
  const setScreen = useActionStore((s: any) => s.setScreen);
  const activeChallenge = useActionStore((s: any) => s.activeChallenge);
  const addAction = useActionStore((s: any) => s.addAction);

  return (
    <div className="flex h-screen w-screen bg-gray-100 overflow-hidden">

      {/* Left Half: UI and Controls */}
      <div className="w-1/2 flex flex-col border-r-4 border-gray-300 bg-white">

        {/* Header */}
        <div className="p-4 bg-gray-800 text-white flex items-center justify-between shadow-md z-10 shrink-0">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setScreen('challenge-info')}
              className="bg-gray-600 hover:bg-gray-500 px-3 py-1 rounded"
            >
              ← Abort
            </button>
            <h2 className="text-xl font-bold truncate">{activeChallenge?.title}</h2>
          </div>
          <span className="text-gray-300 text-sm hidden sm:inline">Challenge Mode</span>
        </div>

        {/* Sequence Area (Takes up remaining middle space, guaranteed at least 200px tall) */}
        <div className="flex-1 p-4 overflow-auto bg-gray-50 relative min-h-[200px] flex flex-col">
          
          {/* Sequence Toolbar: Clear Button placed here */}
          <div className="mb-4 flex justify-start">
            <ClearButton />
          </div>

          <ActionBlockSeqList />
        </div>

        {/* Footer Blocks (Pinned to bottom, restricted to 45% of screen height) */}
        <div className="border-t border-gray-200 shrink-0 overflow-y-auto max-h-[45vh]">
          <Footer addBlock={(block) => addAction(block)} />
        </div>

      </div>

      {/* Right Half: The Canvas Map */}
      <div className="w-1/2 relative bg-[#2c3e50] flex flex-col items-center justify-center p-4">
        <div className="relative w-full h-full border-4 border-gray-700 rounded-xl overflow-hidden bg-white shadow-2xl">
          <RobotSimulationOverlay isSplitScreen={true} />
        </div>
      </div>

    </div>
  );
}
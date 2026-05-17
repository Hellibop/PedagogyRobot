import React from 'react';
import { useActionStore } from '../actionStore';

export default function ChallengeInfo() {
  const setScreen = useActionStore((s: any) => s.setScreen);
  const activeChallenge = useActionStore((s: any) => s.activeChallenge);

  if (!activeChallenge) return null;

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-8">
      <div className="w-full max-w-3xl flex items-center mb-8">
        <button
          onClick={() => setScreen('challenge-list')}
          className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded shadow transition"
        >
          ← Back to List
        </button>
      </div>

      <div className="bg-white p-10 rounded-2xl shadow-lg max-w-2xl w-full text-center mt-10">
        <h1 className="text-4xl font-bold text-gray-800 mb-4">{activeChallenge.title}</h1>
        <span className={`inline-block mb-8 px-4 py-1 text-sm font-bold rounded-full text-white ${activeChallenge.diff === 'Easy' ? 'bg-green-500' : activeChallenge.diff === 'Medium' ? 'bg-yellow-500 text-gray-900' : 'bg-red-500'
          }`}>
          {activeChallenge.diff} Difficulty
        </span>

        <p className="text-xl text-gray-600 leading-relaxed mb-10">
          {activeChallenge.desc}
        </p>

        <button
          onClick={() => setScreen('challenge-play')}
          className="bg-blue-600 hover:bg-blue-700 text-white text-xl font-bold px-10 py-4 rounded-xl shadow-lg transition"
        >
          Accept Challenge →
        </button>
      </div>
    </div>
  );
}
import React from 'react';
import { useActionStore } from '../actionStore';
import { CHALLENGES } from '../dataclasses/ChallengeData';

export default function ChallengeList() {
  const setScreen = useActionStore((s: any) => s.setScreen);

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-8">
      <div className="w-full max-w-3xl flex items-center mb-8">
        <button
          onClick={() => setScreen('sandbox')}
          className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded shadow transition"
        >
          ← Back to Sandbox
        </button>
        <h1 className="text-3xl font-bold text-gray-800 ml-6">Select a Challenge</h1>
      </div>

      <div className="w-full max-w-3xl flex flex-col gap-4">
        {CHALLENGES.map((c) => (
          <div
            key={c.id}
            onClick={() => setScreen('challenge-info', c)}
            className="bg-white p-6 rounded-xl shadow cursor-pointer hover:shadow-md hover:-translate-y-1 transition border-l-4 border-blue-500 flex justify-between items-center"
          >
            <div>
              <h2 className="text-xl font-bold text-gray-800">Challenge {c.id}: {c.title}</h2>
              <span className={`inline-block mt-2 px-3 py-1 text-sm font-bold rounded-full text-white ${c.diff === 'Easy' ? 'bg-green-500' : c.diff === 'Medium' ? 'bg-yellow-500 text-gray-900' : 'bg-red-500'
                }`}>
                {c.diff}
              </span>
            </div>
            <div className="text-gray-400 text-2xl">→</div>
          </div>
        ))}
      </div>
    </div>
  );
}
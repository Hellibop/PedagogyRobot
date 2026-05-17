import React from 'react';
import { useActionStore } from './actionStore';
import SandboxApp from './screens/SandboxApp';
import ChallengeList from './screens/ChallengeList';
import ChallengeInfo from './screens/ChallengeInfo';
import ChallengePlay from './screens/ChallengePlay';

export default function App() {
  const currentScreen = useActionStore((s: any) => s.currentScreen || 'sandbox');

  switch (currentScreen) {
    case 'challenge-list': return <ChallengeList />;
    case 'challenge-info': return <ChallengeInfo />;
    case 'challenge-play': return <ChallengePlay />;
    case 'sandbox':
    default:
      return <SandboxApp />;
  }
}
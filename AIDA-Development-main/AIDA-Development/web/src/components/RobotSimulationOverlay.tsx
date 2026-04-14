import React, { useEffect, useRef } from 'react';
import { useActionStore } from '../actionStore';

const CELL_SIZE = 40;
const GRID_WIDTH = 15;
const GRID_HEIGHT = 15;
const SPEED = 2;
const TURN_SPEED = 0.05;
const PAUSE_FRAMES = 40;

const COMMAND_DICT: Record<string, any> = {
  'Forward': { type: 'move', dist: 1 },
  'Long Forward': { type: 'move', dist: 3 },
  'Backwards': { type: 'move', dist: -1 },
  'Long Backward': { type: 'move', dist: -3 },
  'Left': { type: 'turn', angle: -Math.PI / 2 },
  'Right': { type: 'turn', angle: Math.PI / 2 },
  'Input Gesture': { type: 'wait', frames: 60 },
  'Input Sound': { type: 'wait', frames: 60 },
  'Input Voice': { type: 'wait', frames: 60 },
};

// Accept the isSplitScreen prop (defaults to false for Sandbox)
export const RobotSimulationOverlay: React.FC<{ isSplitScreen?: boolean }> = ({ isSplitScreen = false }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const playing = useActionStore((s: any) => s.playing);
  const actions = useActionStore((s: any) => s.actions);
  const currentIndex = useActionStore((s: any) => s.currentIndex);
  const step = useActionStore((s: any) => s.step);
  const stop = useActionStore((s: any) => s.stop);

  const simState = useRef({
    x: Math.floor(GRID_WIDTH / 2) * CELL_SIZE,
    y: Math.floor(GRID_HEIGHT / 2) * CELL_SIZE,
    targetX: 0, targetY: 0,
    angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
    stateMachine: 'IDLE',
    timer: 0,
    lastProcessedIndex: -1
  });

  useEffect(() => {
    if (!playing) return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let animationFrameId: number;

    const drawRobot = (x: number, y: number, angle: number) => {
      if (!ctx) return;
      const offset = CELL_SIZE / 2;
      ctx.save();
      ctx.translate(x + offset, y + offset);
      ctx.rotate(angle);

      ctx.fillStyle = '#7f8c8d';
      ctx.fillRect(-16, -18, 32, 8);
      ctx.fillRect(-16, 10, 32, 8);
      ctx.fillStyle = '#e67e22';
      ctx.fillRect(-12, -12, 24, 24);
      ctx.strokeStyle = '#d35400';
      ctx.lineWidth = 2;
      ctx.strokeRect(-12, -12, 24, 24);
      ctx.fillStyle = '#f1c40f';
      ctx.beginPath();
      ctx.arc(8, 0, 4, 0, Math.PI * 2);
      ctx.fill();

      ctx.restore();
    };

    const loop = () => {
      if (!ctx || !canvas) return;
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      const state = simState.current;

      if (state.stateMachine === 'IDLE' && currentIndex < actions.length && currentIndex !== state.lastProcessedIndex) {
        const actionTitle = actions[currentIndex].action.title;
        const cmdData = COMMAND_DICT[actionTitle];

        state.lastProcessedIndex = currentIndex;

        if (!cmdData) {
          step();
        } else if (cmdData.type === 'move') {
          state.targetX = state.x + Math.cos(state.angle) * (cmdData.dist * CELL_SIZE);
          state.targetY = state.y + Math.sin(state.angle) * (cmdData.dist * CELL_SIZE);
          state.stateMachine = 'EXECUTING_MOVE';
        } else if (cmdData.type === 'turn') {
          state.targetAngle = state.angle + cmdData.angle;
          state.stateMachine = 'EXECUTING_TURN';
        } else if (cmdData.type === 'wait') {
          state.timer = cmdData.frames;
          state.stateMachine = 'EXECUTING_WAIT';
        }
      }
      else if (state.stateMachine === 'IDLE' && currentIndex >= actions.length) {
        stop();
      }

      if (state.stateMachine === 'EXECUTING_MOVE') {
        const dx = state.targetX - state.x;
        const dy = state.targetY - state.y;
        const dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < SPEED) {
          state.x = state.targetX;
          state.y = state.targetY;
          state.timer = PAUSE_FRAMES;
          state.stateMachine = 'POST_DELAY';
        } else {
          state.x += (dx / dist) * SPEED;
          state.y += (dy / dist) * SPEED;
        }
      }
      else if (state.stateMachine === 'EXECUTING_TURN') {
        const diff = state.targetAngle - state.angle;
        if (Math.abs(diff) < TURN_SPEED) {
          state.angle = state.targetAngle;
          state.timer = PAUSE_FRAMES;
          state.stateMachine = 'POST_DELAY';
        } else {
          state.angle += Math.sign(diff) * TURN_SPEED;
        }
      }
      else if (state.stateMachine === 'EXECUTING_WAIT') {
        state.timer--;
        if (state.timer <= 0) {
          state.timer = PAUSE_FRAMES;
          state.stateMachine = 'POST_DELAY';
        }
      }
      else if (state.stateMachine === 'POST_DELAY') {
        state.timer--;
        if (state.timer <= 0) {
          state.stateMachine = 'IDLE';
          step();
        }
      }

      drawRobot(state.x, state.y, state.angle);
      animationFrameId = requestAnimationFrame(loop);
    };

    loop();
    return () => cancelAnimationFrame(animationFrameId);
  }, [playing, actions, currentIndex, step, stop]);

  useEffect(() => {
    if (!playing) {
      simState.current = {
        x: Math.floor(GRID_WIDTH / 2) * CELL_SIZE,
        y: Math.floor(GRID_HEIGHT / 2) * CELL_SIZE,
        targetX: 0, targetY: 0,
        angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
        stateMachine: 'IDLE', timer: 0, lastProcessedIndex: -1
      };
    }
  }, [playing]);

  if (!playing) return null;

  return (
    <div style={{
      position: isSplitScreen ? 'absolute' : 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: isSplitScreen ? 'transparent' : 'rgba(44, 62, 80, 0.98)',
      zIndex: 9999,
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center'
    }}>

      { }
      {!isSplitScreen && (
        <button
          onClick={stop}
          className="absolute top-6 right-6 bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-6 rounded-lg shadow-xl text-lg transition pointer-events-auto"
        >
          ✖ Stop & Close
        </button>
      )}

      {/* Only show title in Sandbox mode */}
      {!isSplitScreen && (
        <h2 style={{ color: 'white', marginBottom: '20px', fontSize: '2rem', fontWeight: 'bold' }}>
          Live Simulation
        </h2>
      )}

      <canvas
        ref={canvasRef}
        width={GRID_WIDTH * CELL_SIZE}
        height={GRID_HEIGHT * CELL_SIZE}
        style={{
          border: '4px solid #34495e',
          borderRadius: '8px',
          backgroundColor: '#ecf0f1',
          backgroundImage: 'linear-gradient(#bdc3c7 1px, transparent 1px), linear-gradient(90deg, #bdc3c7 1px, transparent 1px)',
          backgroundSize: `${CELL_SIZE}px ${CELL_SIZE}px`,
          maxHeight: '80vh',
          maxWidth: '90vw',
          objectFit: 'contain',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)'
        }}
      />
    </div>
  );
};
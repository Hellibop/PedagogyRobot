import React, { useEffect, useRef, useState } from 'react';
import { useActionStore } from '../actionStore';
import mapBackgroundImage from './map_background.jpg';

// Use separate width and height to fix the image's non-square aspect ratio
const CELL_WIDTH = 46;
const CELL_HEIGHT = 47;
const GRID_WIDTH = 18;
const GRID_HEIGHT = 25;
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

const COLLISION_GRID = [
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1],
  [0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0],
  [0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0],
  [0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0],
  [0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0],
  [0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0],
  [0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
];

export const RobotSimulationOverlay: React.FC<{ isSplitScreen?: boolean }> = ({ isSplitScreen = false }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const playing = useActionStore((s: any) => s.playing);
  const actions = useActionStore((s: any) => s.actions);
  const currentIndex = useActionStore((s: any) => s.currentIndex);
  const step = useActionStore((s: any) => s.step);
  const stop = useActionStore((s: any) => s.stop);

  const [mapLoaded, setMapLoaded] = useState(false);
  const mapImageRef = useRef<HTMLImageElement | null>(null);

  const simState = useRef({
    x: 2 * CELL_WIDTH,
    y: 21 * CELL_HEIGHT,
    targetX: 0, targetY: 0,
    angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
    stateMachine: 'IDLE',
    timer: 0,
    lastProcessedIndex: -1
  });

  useEffect(() => {
    const img = new Image();
    img.src = mapBackgroundImage;
    img.onload = () => {
      mapImageRef.current = img;
      setMapLoaded(true);
    };
  }, []);

  useEffect(() => {
    if (!playing) {
      simState.current = {
        x: 2 * CELL_WIDTH,
        y: 21 * CELL_HEIGHT,
        targetX: 0, targetY: 0,
        angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
        stateMachine: 'IDLE', timer: 0, lastProcessedIndex: -1
      };
    }
  }, [playing]);

  useEffect(() => {
    if (!mapLoaded) return;
    if (!isSplitScreen && !playing) return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let animationFrameId: number;

    const drawRobot = (x: number, y: number, angle: number) => {
      if (!ctx) return;
      const offsetX = CELL_WIDTH / 2;
      const offsetY = CELL_HEIGHT / 2;

      ctx.save();
      ctx.translate(x + offsetX, y + offsetY);
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

    const isWalkable = (gridX: number, gridY: number) => {
      if (gridY < 0 || gridY >= GRID_HEIGHT || gridX < 0 || gridX >= GRID_WIDTH) return false;
      return COLLISION_GRID[gridY][gridX] === 1;
    };

    const loop = () => {
      if (!ctx || !canvas) return;
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      const MAP_OFFSET_X = 0;
      const MAP_OFFSET_Y = 0;
      const MAP_STRETCH_X = 0;
      const MAP_STRETCH_Y = 0;

      const SHOW_DEBUG_GRID = true; // Change this to `false` once grid match

      if (mapImageRef.current) {
        ctx.drawImage(
          mapImageRef.current,
          MAP_OFFSET_X,
          MAP_OFFSET_Y,
          canvas.width + MAP_STRETCH_X,
          canvas.height + MAP_STRETCH_Y
        );
      }

      // Draw the Debug Grid using the new non-square rectangular cells!
      if (SHOW_DEBUG_GRID) {
        ctx.strokeStyle = 'rgba(255, 0, 0, 0.4)';
        ctx.lineWidth = 1;
        for (let i = 0; i < canvas.width; i += CELL_WIDTH) { ctx.beginPath(); ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height); ctx.stroke(); }
        for (let i = 0; i < canvas.height; i += CELL_HEIGHT) { ctx.beginPath(); ctx.moveTo(0, i); ctx.lineTo(canvas.width, i); ctx.stroke(); }
      }

      const state = simState.current;

      if (playing) {
        if (state.stateMachine === 'IDLE' && currentIndex < actions.length && currentIndex !== state.lastProcessedIndex) {
          const actionTitle = actions[currentIndex].action.title;
          const cmdData = COMMAND_DICT[actionTitle];
          state.lastProcessedIndex = currentIndex;

          if (!cmdData) {
            step();
          } else if (cmdData.type === 'move') {
            // Apply distinct width/height calculations
            const intendedX = Math.round((state.x + Math.cos(state.angle) * (cmdData.dist * CELL_WIDTH)) / CELL_WIDTH);
            const intendedY = Math.round((state.y + Math.sin(state.angle) * (cmdData.dist * CELL_HEIGHT)) / CELL_HEIGHT);

            if (isWalkable(intendedX, intendedY)) {
              state.targetX = intendedX * CELL_WIDTH;
              state.targetY = intendedY * CELL_HEIGHT;
              state.stateMachine = 'EXECUTING_MOVE';
            } else {
              console.warn("Hit a wall! Waiting in place.");
              state.timer = PAUSE_FRAMES;
              state.stateMachine = 'POST_DELAY';
            }
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
            state.x = state.targetX; state.y = state.targetY;
            state.timer = PAUSE_FRAMES; state.stateMachine = 'POST_DELAY';
          } else {
            state.x += (dx / dist) * SPEED; state.y += (dy / dist) * SPEED;
          }
        }
        else if (state.stateMachine === 'EXECUTING_TURN') {
          const diff = state.targetAngle - state.angle;
          if (Math.abs(diff) < TURN_SPEED) {
            state.angle = state.targetAngle;
            state.timer = PAUSE_FRAMES; state.stateMachine = 'POST_DELAY';
          } else {
            state.angle += Math.sign(diff) * TURN_SPEED;
          }
        }
        else if (state.stateMachine === 'EXECUTING_WAIT') {
          state.timer--;
          if (state.timer <= 0) { state.timer = PAUSE_FRAMES; state.stateMachine = 'POST_DELAY'; }
        }
        else if (state.stateMachine === 'POST_DELAY') {
          state.timer--;
          if (state.timer <= 0) { state.stateMachine = 'IDLE'; step(); }
        }
      }

      drawRobot(state.x, state.y, state.angle);

      animationFrameId = requestAnimationFrame(loop);
    };

    loop();
    return () => cancelAnimationFrame(animationFrameId);
  }, [playing, mapLoaded, actions, currentIndex, step, stop, isSplitScreen]);

  if (!isSplitScreen && !playing) return null;

  return (
    <div style={{
      position: isSplitScreen ? 'absolute' : 'fixed',
      top: 0, left: 0, width: '100%', height: '100%',
      backgroundColor: isSplitScreen ? '#2c3e50' : 'rgba(44, 62, 80, 0.98)',
      zIndex: 9999, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center'
    }}>
      {!isSplitScreen && (
        <button onClick={stop} className="absolute top-6 right-6 bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-6 rounded-lg shadow-xl text-lg transition pointer-events-auto z-50">
          ✖ Stop & Close
        </button>
      )}

      {!mapLoaded ? (
        <h2 style={{ color: 'white' }}>Loading Map...</h2>
      ) : (
        <canvas
          ref={canvasRef}
          width={GRID_WIDTH * CELL_WIDTH}
          height={GRID_HEIGHT * CELL_HEIGHT}
          style={{
            border: isSplitScreen ? 'none' : '4px solid #34495e',
            borderRadius: isSplitScreen ? '0px' : '8px',
            backgroundColor: '#27ae60',
            maxHeight: isSplitScreen ? '100%' : '80vh',
            maxWidth: isSplitScreen ? '100%' : '90vw',
            objectFit: 'contain',
            boxShadow: isSplitScreen ? 'none' : '0 25px 50px -12px rgba(0, 0, 0, 0.5)'
          }}
        />
      )}
    </div>
  );
};
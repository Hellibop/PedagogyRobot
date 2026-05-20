import React, { useEffect, useRef, useState } from 'react';
import { useActionStore } from '../actionStore';
import mapBackgroundImage from './map_background.jpg';


// Use separate width and height to fix the image's non-square aspect ratio
const CELL_WIDTH = 39;
const CELL_HEIGHT = 37.7;
const GRID_WIDTH = 21;
const GRID_HEIGHT = 30;


// --- NEW STUFF: Sandbox Mode variables (No map, pure pixels) ---
const SANDBOX_CELL_SIZE = 40;


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
  [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0],
  [0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0],
  [0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0],
  [0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0],
  [0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0],
  [0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0],
  [0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0],
  [0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0],
  [0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0],
  [0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0],
  [0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0],
  [0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0],
  [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
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


  // We manage visibility internally to allow the robot to finish animating before closing
  const [isVisible, setIsVisible] = useState(false);
  const prevPlaying = useRef(playing);


  // --- NEW STUFF: Dynamic Start Coordinates based on mode ---
  const getStartPosition = () => {
    if (isSplitScreen) {
      return { x: 1 * CELL_WIDTH, y: 21 * CELL_HEIGHT };
    } else {
      return { x: Math.floor(15 / 2) * SANDBOX_CELL_SIZE, y: Math.floor(15 / 2) * SANDBOX_CELL_SIZE };
    }
  };


  const simState = useRef({
    ...getStartPosition(),
    targetX: 0, targetY: 0,
    angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
    stateMachine: 'IDLE',
    nextStateMachine: 'IDLE', // Used to store what to do after PRE_DELAY
    timer: 0,
    preDelayTimer: 0, // Separate timer to not overwrite wait command data
    lastProcessedIndex: -1,
    forceStop: false, // New flag to signal the loop to close down
    hasStarted: false // Flag to track if we've done our initial delay
  });


  // --- NEW STUFF: Delay is now handled internally inside the canvas loop ---
  useEffect(() => {
    if (playing && !prevPlaying.current) {
      // Just turned ON
      setIsVisible(true);
      // Force reset when hitting play. Notice we start immediately at 'IDLE' to catch the index!
      simState.current = {
        ...getStartPosition(),
        targetX: 0, targetY: 0,
        angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
        stateMachine: 'IDLE',
        nextStateMachine: 'IDLE',
        timer: 0,
        preDelayTimer: 0,
        lastProcessedIndex: -1,
        forceStop: false,
        hasStarted: isSplitScreen ? true : false // If splitscreen, skip the delay entirely
      };
    } else if (!playing && prevPlaying.current) {
      // Just turned OFF - Let the loop finish gracefully
      // simState.current.forceStop = true; // <-- COMMENTED OUT: Never force stop automatically
    }


    prevPlaying.current = playing;
  }, [playing, isSplitScreen]);


  useEffect(() => {
    // --- NEW STUFF: Only load the map image if we are in Challenge Mode ---
    if (isSplitScreen) {
      const img = new Image();
      img.src = mapBackgroundImage;
      img.onload = () => {
        mapImageRef.current = img;
        setMapLoaded(true);
      };
    } else {
      setMapLoaded(true);
    }
  }, [isSplitScreen]);


  useEffect(() => {
    if (!mapLoaded) return;


    // In SplitScreen, don't care about isVisible, always run the loop!
    // In Sandbox, we wait until isVisible becomes true to render anything.
    if (!isSplitScreen && !isVisible) return;


    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let animationFrameId: number;


    // --- NEW STUFF: Use current cell sizes based on mode ---
    const currentCellWidth = isSplitScreen ? CELL_WIDTH : SANDBOX_CELL_SIZE;
    const currentCellHeight = isSplitScreen ? CELL_HEIGHT : SANDBOX_CELL_SIZE;


    const drawRobot = (x: number, y: number, angle: number) => {
      if (!ctx) return;
      const offsetX = currentCellWidth / 2;
      const offsetY = currentCellHeight / 2;


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
      // --- NEW STUFF: In Sandbox mode, everything is walkable, No walls. ---
      if (!isSplitScreen) return true;


      if (gridY < 0 || gridY >= GRID_HEIGHT || gridX < 0 || gridX >= GRID_WIDTH) return false;
      return COLLISION_GRID[gridY][gridX] === 1;
    };


    const loop = () => {
      if (!ctx || !canvas) return;
      ctx.clearRect(0, 0, canvas.width, canvas.height);


      if (isSplitScreen) {
        const MAP_OFFSET_X = 0;
        const MAP_OFFSET_Y = 16 + 5;
        const MAP_STRETCH_X = 6;
        const MAP_STRETCH_Y = -15;


        const SHOW_DEBUG_GRID = false;


        if (mapImageRef.current) {
          ctx.drawImage(
            mapImageRef.current,
            MAP_OFFSET_X,
            MAP_OFFSET_Y,
            canvas.width + MAP_STRETCH_X,
            canvas.height + MAP_STRETCH_Y
          );
        }


        if (SHOW_DEBUG_GRID) {
          ctx.strokeStyle = 'rgba(255, 0, 0, 0.4)';
          ctx.lineWidth = 1;
          for (let i = 0; i < canvas.width; i += CELL_WIDTH) { ctx.beginPath(); ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height); ctx.stroke(); }
          for (let i = 0; i < canvas.height; i += CELL_HEIGHT) { ctx.beginPath(); ctx.moveTo(0, i); ctx.lineTo(canvas.width, i); ctx.stroke(); }
        }
      } else {
        ctx.fillStyle = '#ecf0f1';
        ctx.fillRect(0, 0, canvas.width, canvas.height);


        ctx.fillStyle = '#bdc3c7';
        for (let x = 0; x < canvas.width; x += SANDBOX_CELL_SIZE) {
          for (let y = 0; y < canvas.height; y += SANDBOX_CELL_SIZE) {
            ctx.beginPath();
            ctx.arc(x, y, 2, 0, Math.PI * 2);
            ctx.fill();
          }
        }
      }


      const state = simState.current;


      if (state.stateMachine === 'IDLE') {
        if (state.forceStop) {
          // setIsVisible(false); // <-- COMMENTED OUT so it doesn't auto-close
          state.forceStop = false;


          if (!isSplitScreen) {
            // return; // <-- COMMENTED OUT so the loop keeps rendering the final frame
          } else {
            Object.assign(state, {
              ...getStartPosition(),
              targetX: 0, targetY: 0,
              angle: -Math.PI / 2, targetAngle: -Math.PI / 2,
              stateMachine: 'IDLE',
              nextStateMachine: 'IDLE',
              timer: 0,
              preDelayTimer: 0,
              lastProcessedIndex: -1,
              hasStarted: true // Resetting after finish skips delay
            });
          }
        } else if (currentIndex < actions.length && currentIndex !== state.lastProcessedIndex) {
          const actionTitle = actions[currentIndex].action.title;
          const cmdData = COMMAND_DICT[actionTitle];
          state.lastProcessedIndex = currentIndex;


          let nextState = 'IDLE';


          if (!cmdData) {
            step();
          } else if (cmdData.type === 'move') {
            const intendedX = Math.round((state.x + Math.cos(state.angle) * (cmdData.dist * currentCellWidth)) / currentCellWidth);
            const intendedY = Math.round((state.y + Math.sin(state.angle) * (cmdData.dist * currentCellHeight)) / currentCellHeight);


            if (isWalkable(intendedX, intendedY)) {
              state.targetX = intendedX * currentCellWidth;
              state.targetY = intendedY * currentCellHeight;
              nextState = 'EXECUTING_MOVE';
            } else {
              console.warn("Hit a wall! Waiting in place.");
              state.timer = PAUSE_FRAMES;
              nextState = 'POST_DELAY';
            }
          } else if (cmdData.type === 'turn') {
            state.targetAngle = state.angle + cmdData.angle;
            nextState = 'EXECUTING_TURN';
          } else if (cmdData.type === 'wait') {
            state.timer = cmdData.frames;
            nextState = 'EXECUTING_WAIT';
          }


          // FIX: Apply our 1-second visual delay directly within the loop on the very first run!
          if (!state.hasStarted && !isSplitScreen && nextState !== 'IDLE') {
            state.hasStarted = true;
            state.stateMachine = 'PRE_DELAY';
            state.nextStateMachine = nextState;
            state.preDelayTimer = 60; // 60 frames = ~1 sec at 60fps
          } else if (nextState !== 'IDLE') {
            state.stateMachine = nextState;
          }
        } else if (currentIndex >= actions.length) {
          // FIX: Wait for the final command to finish visually before calling stop()
          if (state.lastProcessedIndex === actions.length - 1) {
            stop();
          }
        }
      }


      // NEW PRE_DELAY STATE
      if (state.stateMachine === 'PRE_DELAY') {
        state.preDelayTimer--;
        if (state.preDelayTimer <= 0) {
          state.stateMachine = state.nextStateMachine;
        }
      }
      else if (state.stateMachine === 'EXECUTING_MOVE') {
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
  }, [isVisible, mapLoaded, actions, currentIndex, step, stop, isSplitScreen]);


  if (!isSplitScreen && !isVisible) return null;


  return (
    <div style={{
      position: isSplitScreen ? 'absolute' : 'fixed',
      top: 0, left: 0, width: '100%', height: '100%',
      backgroundColor: isSplitScreen ? '#2c3e50' : 'rgba(44, 62, 80, 0.98)',
      zIndex: 9999, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center'
    }}>
      {!isSplitScreen && (
        <button onClick={() => { setIsVisible(false); stop(); }} className="absolute top-6 right-6 bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-6 rounded-lg shadow-xl text-lg transition pointer-events-auto z-50">
          ✖ Stop & Close
        </button>
      )}


      {!mapLoaded ? (
        <h2 style={{ color: 'white' }}>Loading Map...</h2>
      ) : (
        <canvas
          ref={canvasRef}
          width={isSplitScreen ? GRID_WIDTH * CELL_WIDTH : 15 * SANDBOX_CELL_SIZE}
          height={isSplitScreen ? GRID_HEIGHT * CELL_HEIGHT : 15 * SANDBOX_CELL_SIZE}
          style={{
            border: isSplitScreen ? 'none' : '4px solid #34495e',
            borderRadius: isSplitScreen ? '0px' : '8px',
            backgroundColor: isSplitScreen ? '#27ae60' : '#ecf0f1',
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
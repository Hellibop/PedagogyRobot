

import {
  BaseAction,
  GestureAction,
  LoopAction,
  SoundAction,
  VoiceAction,
} from "./ActionData";


/*

 This file defines the types of actions in AIDA.
 Using the BaseAction class, you can create various other actions.

 Most of the times set in duration are just placeholders. We don't know what the robot's going to do or if 
 it is even possible to predict the timings. 


 STEPS TO CREATE A NEW ACTION:

  1.  Decide what type of action you want. Does it have any special features and does it need a popup?
      Create a new class in #ActionData.tsx also make sure to add it in the ActionType enum. 
      Otherwise feel free do define a BaseAction. 

  2.  Add the icon in the #IconMap.tsx

  3.  If it's a simple BaseAction you don't need to do anything here. 
      If it's a special action you need to define the UI-component in a new file under #components/actionblocks/

  4.  If it's a special action, add it to the #Loader.tsx
      Also make sure to add it in #DynamicActionBlock.tsx  (you don't need to do this if its a BaseAction)

  5.  This applies to both special actions and base actions. 
      Should you need extra logic when exporting the action to the app you need to add it in #Parser.tsx. 
      Otherwise, only internal name will be sent. MAKE SURE TO CHANGE THE PARSER ON THE APP TOO.

  6.  Finally add your action to either the #MovementsActionGrid.tsx or #SpecialActionsGrid.tsx.

  7.  You have addded your action. Now add it in the app as well! 

*/ 


//Base Actions.
const longForward: BaseAction = new BaseAction();
longForward.title = "Long Forward";
longForward.duration = 5.0;
longForward.icon = "LucideArrowUp";
longForward.internalName = "lfwd";

const longBackwards: BaseAction = new BaseAction();
longBackwards.title = "Long Backward";
longBackwards.duration = 5.0;
longBackwards.icon = "LucideArrowDown";
longBackwards.internalName = "lbwd";

const forward: BaseAction = new BaseAction();
forward.title = "Forward";
forward.duration = 2.0;
forward.icon = "LucideArrowUp";
forward.internalName = "fwd";

const backward: BaseAction = new BaseAction();
backward.title = "Backwards";
backward.duration = 2.0;
backward.icon = "LucideArrowDown";
backward.internalName = "bwd";

const left: BaseAction = new BaseAction();
left.title = "Left";
left.duration = 2.0;
left.icon = "LucideArrowLeft";
left.internalName = "lef";

const longLeft: BaseAction = new BaseAction();
longLeft.title = "Long Left";
longLeft.duration = 5.0;
longLeft.icon = "LucideArrowLeft";
longLeft.internalName = "llef";

const right: BaseAction = new BaseAction();
right.title = "Right";
right.duration = 2.0;
right.icon = "LucideArrowRight";
right.internalName = "rig";

const longRight: BaseAction = new BaseAction();
longRight.title = "Long Right";
longRight.duration = 5.0;
longRight.icon = "LucideArrowRight";
longRight.internalName = "lrig";


//Special Actions
const loopStart: LoopAction = new LoopAction();
loopStart.title = "Loop";
loopStart.duration = 0.0;
loopStart.icon = "LucideRefreshCcw";
loopStart.internalName = "lps";
loopStart.iterations = 1;

const loopEnd: LoopAction = new LoopAction();
loopEnd.title = "Loop End";
loopEnd.duration = 0.0;
loopEnd.icon = "LucideRefreshCcw";
loopEnd.internalName = "lpe";
loopEnd.isEnd = true;

const inputGesture: GestureAction = new GestureAction();
inputGesture.title = "Input Gesture";
inputGesture.duration = 2.0; 
inputGesture.icon = "LucideHand";
inputGesture.internalName = "ipg";

const inputVoice: VoiceAction = new VoiceAction();
inputVoice.title = "Input Voice";
inputVoice.duration = 2.0; 
inputVoice.icon = "LucideMegaphone";
inputVoice.internalName = "ipv";

const inputSound: SoundAction = new SoundAction();
inputSound.title = "Input Sound";
inputSound.duration = 2.0; 
inputSound.icon = "LucideMusic2";
inputSound.internalName = "imu";


//Specific Gestures.
const thumbsUp: GestureAction = new GestureAction();
thumbsUp.title = "Thumbs Up";
thumbsUp.isSet = true;
thumbsUp.duration = 2.0;
thumbsUp.icon = "LucideThumbsUp";
thumbsUp.internalName = "gstu";

const thumbsDown: GestureAction = new GestureAction();
thumbsDown.title = "Thumbs Down";
thumbsDown.isSet = true;
thumbsDown.duration = 2.0;
thumbsDown.icon = "LucideThumbsDown";
thumbsDown.internalName = "gstd";

const point: GestureAction = new GestureAction();
point.title = "Point";
point.isSet = true;
point.duration = 2.0;
point.internalName = "gspo";
point.icon = "LucidePointer";

const gun: GestureAction = new GestureAction();
gun.title = "Finger Gun";
gun.isSet = true;
gun.duration = 2.0;
gun.internalName = "gsfg";
gun.icon = "LucideFingerprint";

const waving: GestureAction = new GestureAction();
waving.title = "Waving";
waving.isSet = true;
waving.duration = 2.0;
waving.internalName = "gswv";
waving.icon = "LucideWaves";

const stopSign: GestureAction = new GestureAction();
stopSign.title = "Stop";
stopSign.isSet = true;
stopSign.duration = 2.0;
stopSign.internalName = "gsst";
stopSign.icon = "LucideStopCircle";


//Specific sounds.
export const eightBitLaser: SoundAction = new SoundAction();
eightBitLaser.title = "Eight Bit Laser";
eightBitLaser.internalName = "sebl";
eightBitLaser.isSet = true;
eightBitLaser.duration = 1.5;
eightBitLaser.icon = "LucideMusic2";

export const beepingRobotMachine: SoundAction = new SoundAction();
beepingRobotMachine.title = "Beeping Robot Machine";
beepingRobotMachine.internalName = "sbrm";
beepingRobotMachine.isSet = true;
beepingRobotMachine.duration = 2.0;
beepingRobotMachine.icon = "LucideMusic2";

export const robotPowerOff: SoundAction = new SoundAction();
robotPowerOff.title = "Robot Power Off";
robotPowerOff.internalName = "srpo";
robotPowerOff.isSet = true;
robotPowerOff.duration = 3.0;
robotPowerOff.icon = "LucideMusic2";

export const mechanicalClamp: SoundAction = new SoundAction();
mechanicalClamp.title = "Mechanical Clamp";
mechanicalClamp.internalName = "smec";
mechanicalClamp.isSet = true;
mechanicalClamp.duration = 2.5;
mechanicalClamp.icon = "LucideMusic2";

export const robotCall: SoundAction = new SoundAction();
robotCall.title = "Robot Call";
robotCall.internalName = "sroc";
robotCall.isSet = true;
robotCall.duration = 2.0;
robotCall.icon = "LucideMusic2";

export const robotDrum: SoundAction = new SoundAction();
robotDrum.title = "Robot Drum";
robotDrum.internalName = "srod";
robotDrum.isSet = true;
robotDrum.duration = 2.5;
robotDrum.icon = "LucideMusic2";

export {
  longForward,
  longBackwards,
  forward,
  backward,
  left,
  longLeft,
  right,
  longRight,
  loopStart,
  loopEnd,
  inputGesture,
  inputVoice,
  inputSound,
  thumbsUp,
  thumbsDown,
  point,
  gun,
  stopSign,
  waving,
};

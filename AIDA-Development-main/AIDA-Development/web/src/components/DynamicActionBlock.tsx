import { BaseAction, GestureAction, LoopAction, SoundAction, VoiceAction } from '../dataclasses/ActionData';
import { ActionBlockSeq } from './actionblocks/ActionBlockSeq';
import { GestureBlockSeq } from './actionblocks/GestureBlockSeq';
import { LoopEndBlockSeq } from './actionblocks/LoopEndBlockSeq';
import { LoopStartBlockSeq } from './actionblocks/LoopStartBlockSeq';
import { SoundBlockSeq } from './actionblocks/SoundBlockSeq';
import { VoiceBlockSeq } from './actionblocks/VoiceBlockSeq';

/**
 * Component: DynamicActionBlock
 *
 * Renders a dynamic action block based on the type of the action.
 * Supports LoopAction (start/end), GestureAction, SoundAction, and VoiceAction.
 * Defaults to a basic ActionBlockSeq for other types.
 *
 * @param {{ action: BaseAction, uid: number }} props - The action data and its uid.
 * @returns {JSX.Element} The specific action block component for the provided action.
 */
export const DynamicActionBlock = ({ action, uid }: { action: BaseAction, uid: number }) => {
  if (action instanceof LoopAction) {
    if ((action as LoopAction).isEnd) {
      return <LoopEndBlockSeq action={action as LoopAction} closing={() => { }} uid={uid} />;
    } else {
      return <LoopStartBlockSeq action={action as LoopAction} closing={() => { }} uid={uid} />;
    }
  } else if (action instanceof GestureAction) {
    return <GestureBlockSeq action={action as GestureAction} closing={() => { }} uid={uid} />;
  } else if (action instanceof SoundAction) {
    return <SoundBlockSeq action={action as SoundAction} closing={() => { }} uid={uid} />;
  } else if (action instanceof VoiceAction) {
    return <VoiceBlockSeq action={action as VoiceAction} uid={uid} />;
  }
  return <ActionBlockSeq action={action} uid={uid} />;
};
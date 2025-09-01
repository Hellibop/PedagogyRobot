import { BaseAction, GestureAction, LoopAction, SoundAction, VoiceAction } from '../dataclasses/ActionData';
import { ActionBlockSeq } from './actionblocks/ActionBlockSeq';
import { GestureBlockSeq } from './actionblocks/GestureBlockSeq';
import { LoopEndBlockSeq } from './actionblocks/LoopEndBlockSeq';
import { LoopStartBlockSeq } from './actionblocks/LoopStartBlockSeq';
import { SoundBlockSeq } from './actionblocks/SoundBlockSeq';

/**
 * Component: DynamicActionBlock
 *
 * Renders a dynamic action block based on the type of the action.
 * Supports LoopAction (start/end), GestureAction, SoundAction, and VoiceAction (not implemented),
 * defaulting to a basic ActionBlockSeq for other types.
 *
 * @param {{ action: BaseAction, uid: number }} props - The action data and its uid.
 * @returns {JSX.Element} The specific action block component for the provided action.
 */
export const DynamicActionBlock = ({ action, uid }: { action: BaseAction, uid: number }) => {
  if (action instanceof LoopAction) {
    if (action.isEnd) {
      return (
        <LoopEndBlockSeq action={action} closing={() => { }} uid={uid} />
      );
    } else {
      return (
        <LoopStartBlockSeq action={action} closing={() => { }} uid={uid} />
      );
    }
  } else if (action instanceof GestureAction) {
    return (
      <GestureBlockSeq action={action} closing={() => { }} uid={uid} />
    );
  } else if (action instanceof SoundAction) {
    return (
      <SoundBlockSeq action={action} closing={() => { }} uid={uid} />
    );
  } else if (action instanceof VoiceAction) {
    // Not implemented.
  }
  return (
    <ActionBlockSeq action={action} uid={uid} />
  );
};

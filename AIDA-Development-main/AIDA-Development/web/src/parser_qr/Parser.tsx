import { BaseAction, LoopAction, VoiceAction } from "../dataclasses/ActionData";
import { ActionDataExtended } from "../dataclasses/ActionDataExtended";
import { useActionStore } from "../actionStore";


/**
 * Parser function to convert the action sequence into a string. 
 * Used later to display a QR-code to be read by the android app version and parsed into the app.
 * 
 * @returns {string} - The parsed string of actions.
 */

export const parser = () => {
  // Get the actions from the Zustand store
  let blocks: ActionDataExtended[] = useActionStore.getState().actions;


  const actions: BaseAction[] = blocks.map(action => action.action);
  let output: string = "";

  actions.forEach(action => {
    if (action instanceof LoopAction) {
      if (!action.isEnd) {
        output += `${action.internalName}-${action.iterations},`;
      } else {
        output += `${action.internalName},`;
      }
    } else if (action instanceof VoiceAction) {
      const msg = action.message?.trim() ?? '';
      output += `${action.internalName}:${encodeURIComponent(msg)},`;
    } else {
      output += `${action.internalName},`;
    }
  });


  console.log(output);
  return output;
};

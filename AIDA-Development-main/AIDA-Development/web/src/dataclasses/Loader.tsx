import { ActionType, LoopAction, SoundAction, GestureAction, BaseAction, VoiceAction } from "./ActionData";
import { ActionDataExtended } from "./ActionDataExtended";


/**
 * 
 * Function to load actions from local storage and parse them into ActionDataExtended objects.
 * Used in ActionStore.tsx to load actions from local storage. 
 * 
 * @returns {ActionDataExtended[]} Array of ActionDataExtended objects 
 */
export const loadActionsFromStorage = (): ActionDataExtended[] => {
  const unparsedActions = localStorage.getItem('action-list');
  const revived: ActionDataExtended[] = [];

  if (!unparsedActions) return [];

  const actions = JSON.parse(unparsedActions) as { action: BaseAction; uid: number }[];

  actions.forEach((a) => {
    let realAction: BaseAction;
    

    switch (a.action.type) {
      case ActionType.Loop:
            realAction = new LoopAction(a.action as LoopAction)
        break;
      case ActionType.Sound:
        realAction = new SoundAction(a.action as SoundAction)
        break;
      case ActionType.Gesture:
        realAction = new GestureAction(a.action as GestureAction)
        break;
      case ActionType.Voice:
        realAction = new VoiceAction(a.action as VoiceAction)
        break;
      case ActionType.Base:
        realAction = a.action;
        break;
      default:
        console.warn("Unknown action type", a);
        return;
    }

    const extended = new ActionDataExtended(realAction);
    extended.uid = a.uid; 
    revived.push(extended);
  });

  return revived;
};

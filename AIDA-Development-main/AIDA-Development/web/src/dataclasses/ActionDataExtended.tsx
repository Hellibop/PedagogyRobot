import { BaseAction } from "./ActionData";


/*
  ActionDataExtended is used in the Sequence to be able to identify blocks by their uid. 
  This is very important because otherwise weird stuff will happen when dragging identical actions in the sequence. 

  TO THE NEXT GROUP:
  I've tried including the uid into the BaseAction class but there was not enough time (there's some branch somehwere where I tried this). 
  I recommend the next group to fix this if they have time since this method is overly complex. 

*/

export class ActionDataExtended {
    action: BaseAction;
    uid: number;
  
    static index: number = Number(localStorage.getItem('uid')) || 0; //Extremly important to fetch the latest used uid from localStorage so we don't duplicate uids.
  
    constructor(action: BaseAction) {
      this.action = action;
      this.uid = ActionDataExtended.index;
      ActionDataExtended.index++;
      localStorage.setItem('uid', String(ActionDataExtended.index));
    }
  }

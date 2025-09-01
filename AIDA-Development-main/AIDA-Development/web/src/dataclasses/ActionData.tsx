/*
    This file contains all the backend logic for the different types of ActionBlock. 
*/

enum ActionType {
    Base,
    Loop,
    Gesture,
    Voice,
    Sound
}

/*
    BaseAction is the superclass of all actions.
    It can be used on its own for simple blocks like "Left", "Right", "Long Forward" etc.
    For Special Actions, use the appropriate subclass. More below. 
*/
class BaseAction {
    title: string = "";
    type: ActionType = ActionType.Base; 
    icon: string = "LucideAArrowUp"; //The name of the icon the action uses. String formt and then make sure to map it in #IconMap.
    duration: number = 0; //The time an action takes to execute.
    durationRemaining: number = 0; //The time remaining of the action.
    height: number = 160;
    width: number = 150; 
    scaling: number = 1; //Unused. Can be used for scaling the UI.
    internalName: string = "nil"; // Internal language name used for QR-code.

    constructor(init?: Partial<BaseAction>) {
        Object.assign(this, init);
        if (this.durationRemaining === undefined) {
            this.durationRemaining = this.duration;
        }
    }
}

/*
    The LoopAction has additional members for handling loops. Take note of the isEnd field. 
*/

class LoopAction extends BaseAction {
    type: ActionType = ActionType.Loop;
    iterations: number = 0;
    isEnd: boolean = false;
    actionsInLoop: BaseAction[] = [];

    constructor(init?: Partial<LoopAction>) {
        super(init);
        Object.assign(this, init);
    }
}

/*
    VoiceActions are currently not implemented in the UI and thus they to do not work.
    UNUSED
*/

class VoiceAction extends BaseAction {
    type: ActionType = ActionType.Voice;
    message: string = "";

    constructor(init?: Partial<VoiceAction>) {
        super(init);
        Object.assign(this, init);
    }
}

/*
    GestureActions contain additional fields for handling Gestures. 
*/

class GestureAction extends BaseAction {
    type: ActionType = ActionType.Gesture;
    isSet: boolean = false;

    constructor(init?: Partial<GestureAction>) {
        super(init);
        Object.assign(this, init);
    }
}

/*
    SoundActions contain additional fields for handling Sounds. 
*/

class SoundAction extends BaseAction {
    type: ActionType = ActionType.Sound;
    isSet: boolean = false;

    constructor(init?: Partial<SoundAction>) {
        super(init);
        Object.assign(this, init);
    }
}

export {
    BaseAction,
    LoopAction,
    VoiceAction,
    GestureAction,
    SoundAction,
    ActionType
};

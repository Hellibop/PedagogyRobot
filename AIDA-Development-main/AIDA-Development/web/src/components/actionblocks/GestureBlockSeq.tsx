import { LucideSettings, LucideX } from "lucide-react";
import { useState } from "react";
import Popup from "reactjs-popup";
import { useActionStore } from "../../actionStore";
import { GestureAction } from "../../dataclasses/ActionData";
import { ActionDataExtended } from "../../dataclasses/ActionDataExtended";
import { thumbsUp, thumbsDown, point, stopSign, gun, waving, inputGesture } from "../../dataclasses/ActionDefinitions";
import { iconMap, IconName } from "../../dataclasses/IconMap";
import { ActionBlock } from "./ActionBlock";

/*

This UI-component is used for Gestures. It has additional features for selecting a gesture inside of it.

Use in: The sequence. 
Use with: GestureAction. 

*/

export function GestureBlockSeq({
    action,
    closing,
    uid,
  }: {
    action: GestureAction;
    closing: Function;
    uid: number;
  }) {
    const [currentAction, setCurrentAction] = useState(action);
    const updateAction = useActionStore((state) => state.updateAction);
  
    const handleGestureChange = (newGesture: GestureAction, close: () => void) => {
      const updated = new ActionDataExtended(newGesture);
      updated.uid = uid;
      updateAction(uid, updated);
      setCurrentAction(newGesture);
      close();
    };
  
    const color: string = currentAction.isSet ? "bg-gray-700" : "bg-neutral-400";
    const IconComponent = iconMap[action.icon as IconName];
  
    return (
      <div
        className={`w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md ${color} text-center cursor-grab active:cursor-grabbing`}
      >
        <div className="relative flex w-full items-center">
          <Popup
            trigger={
              <div onPointerDown={(e) => e.stopPropagation()}>
                <LucideSettings className="absolute left-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
              </div>
            }
            modal
            nested
            overlayStyle={{ zIndex: 9999 }}
            contentStyle={{ zIndex: 10000 }}
          >
            {
              ((close: () => void) => (
                <div className="bg-slate-700 rounded-lg shadow-lg flex flex-col items-center p-6 trasnform scale-80">
                  <div onClick={close} onPointerDown={(e) => e.stopPropagation()}>
                    <LucideX className="absolute right-0 top-0 text-white cursor-pointer" />
                  </div>
                  <div className="bg-white rounded-lg shadow-lg flex flex-col items-center p-10 m-2 space-y-3 space-x-3">
                    <p className="text-black mb-3 text-lg">Select a Gesture</p>
                    <div className="flex flex-row space-x-3">
                      {[thumbsUp, thumbsDown, point].map((gesture, idx) => (
                        <div
                          key={idx}
                          className="cursor-pointer"
                          onPointerDown={(e) => e.stopPropagation()}
                          onClick={() => handleGestureChange(gesture, close)}
                        >
                          <ActionBlock action={gesture} />
                        </div>
                      ))}
                    </div>
                    <div className="flex flex-row space-x-3">
                      {[stopSign, gun, waving].map((gesture, idx) => (
                        <div
                          key={idx}
                          className="cursor-pointer"
                          onPointerDown={(e) => e.stopPropagation()}
                          onClick={() => handleGestureChange(gesture, close)}
                        >
                          <ActionBlock action={gesture} />
                        </div>
                      ))}
                    </div>
                    <div
                      onPointerDown={(e) => e.stopPropagation()}
                      className="mt-3 p-3 rounded-2xl bg-yellow-500 cursor-pointer"
                      onClick={() => handleGestureChange(inputGesture, close)}
                    >
                      <p className="text-white font-bold">Cancel</p>
                    </div>
                  </div>
                </div>
              )) as unknown as React.ReactNode
            }
          </Popup>
          <div
            onClick={() => useActionStore.getState().removeActionByUid(uid)}
            onPointerDown={(e) => e.stopPropagation()}
          >
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
          </div>
        </div>
        <div className="relative">
          <IconComponent className="w-20 h-20 text-white" />
        </div>
        <span className="text-lg font-semibold text-white overflow-hidden">
          {currentAction.title || "EMPTY"}
        </span>
      </div>
    );
  }
  
  
  
  
  
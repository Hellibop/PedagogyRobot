import { LucideSettings, LucideX } from "lucide-react";
import { useState } from "react";
import Popup from "reactjs-popup";
import { useActionStore } from "../../actionStore";
import { SoundAction } from "../../dataclasses/ActionData";
import { robotCall, robotDrum, robotPowerOff, beepingRobotMachine, mechanicalClamp, eightBitLaser, inputSound } from "../../dataclasses/ActionDefinitions";
import { iconMap, IconName } from "../../dataclasses/IconMap";
import { ActionDataExtended } from "../../dataclasses/ActionDataExtended";


/*

This is the UI-component for SoundBlocks. It has additional features for selecting a sound. 
There is a helper function at the bottom of this code to map sounds to the pop-up for easier selection. 

Use in: The sequence.
Use with: SoundBlock.

*/

export function SoundBlockSeq({
    action,
    closing,
    uid,
  }: {
    action: SoundAction;
    closing: Function;
    uid: number,
  }) {
    const [currentAction, setCurrentAction] = useState(action);
    const color: string = currentAction.isSet ? "bg-gray-700" : "bg-neutral-400";
    const sounds: SoundAction[] = [
      robotCall,
      robotDrum,
      robotPowerOff,
      beepingRobotMachine,
      mechanicalClamp,
      eightBitLaser,
    ];
  
  
  
    const IconComponent = iconMap[action.icon as IconName] 
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
            overlayStyle={{
              zIndex: 9999, // Set a high z-index for the overlay
            }}
            contentStyle={{
              zIndex: 10000, // Set a higher z-index for the content
            }}
          >
            {
              ((close: () => void) => (
                <div className="bg-slate-700 rounded-lg shadow-lg flex flex-col items-center p-6">
                  <div onClick={close} onPointerDown={(e) => e.stopPropagation()}>
                    <LucideX className="absolute right-0 top-0 text-white cursor-pointer" />
                  </div>
                  <div className="bg-white rounded-lg shadow-lg flex flex-col items-center p-10 m-2">
                    <p className="text-black mb-3 text-lg">Select a Sound</p>
                    <div className="flex flex-col cursor-pointer">
                      {SoundActionButtons(sounds, uid, setCurrentAction, close)}
                    </div>
                    <div
                      onPointerDown={(e) => e.stopPropagation()}
                      className="mt-3 p-3 rounded-2xl bg-yellow-500 cursor-pointer"
                      onClick={() => {
                        setCurrentAction(inputSound);
                        close();
                      }}
                    >
                      <p className="text-white font-bold">Cancel</p>
                    </div>
                  </div>
                </div>
              )) as unknown as React.ReactNode
            }
          </Popup>
          <div onClick={() => useActionStore.getState().removeActionByUid(uid)} onPointerDown={(e) => e.stopPropagation()}>
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
          </div>
        </div>
        <div className="relative ">
          <IconComponent className="w-20 h-20 text-white" />
        </div>
        <span className="text-lg font-semibold text-white overflow-hidden">
          {currentAction.title || "EMPTY"}
        </span>
      </div>
    );
  }


/*

Se comment at top of file. 

*/

function SoundActionButtons(
  
    sounds: SoundAction[],
    uid: number,
    setCurrentAction: (action: SoundAction) => void,
    close: () => void
  ) {
    const updateAction = useActionStore((state) => state.updateAction);
    return sounds.map((sound, index) => (
      <div
        onPointerDown={(e) => e.stopPropagation()}
        key={index}
        className="bg-slate-500 m-2 p-2 text-center rounded-2xl w-64"
        onClick={() => {
  
          const updated = new ActionDataExtended(sound);
          updated.uid = uid;
          updateAction(uid, updated);
          setCurrentAction(sound);
          close();
        }}
      >
        <p>{sound.title}</p>
      </div>
    ));
  }
  
import { LucideSettings, LucideX, LucideMinus, LucidePlus } from "lucide-react";
import { useState } from "react";
import Popup from "reactjs-popup";
import { useActionStore } from "../../actionStore";
import { LoopAction } from "../../dataclasses/ActionData";
import { iconMap, IconName } from "../../dataclasses/IconMap";

/*

This is the UI-component to be used as a loop start.
Use in: The sequence. 
Use with: LoopAction (Specifically if isEnd is set to false)


*/

export function LoopStartBlockSeq({
    action,
    closing,
    uid,
  }: {
    action: LoopAction;
    closing: Function;
    uid: number,
  }) {
    const IconComponent = iconMap[action.icon as IconName] 
    const [newIterations, setNewIterations] = useState(action.iterations);
  
    const handleIncrement = () => {
      if (newIterations < 10) {
        setNewIterations((prev) => {
          const updated = prev + 1;
          action.iterations = updated;
          return updated;
        });
      }
    };
    
    const handleDecrement = () => {
      if (newIterations > 1) {
        setNewIterations((prev) => {
          const updated = prev - 1;
          action.iterations = updated;
          return updated;
        });
      }
    };
  
    return (
      <div className="w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md bg-gray-700 text-center cursor-grab active:cursor-grabbing">
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
                    <p className="text-black pb-3 text-xl">
                      Select a loop interval
                    </p>
                    <p className="text-black pb-3 text-2xl font-bold">
                      {newIterations.toString()}
                    </p>
                    <div className="flex flex-row mb-4 gap-4">
                      <div
                        className="p-3 bg-red-400 rounded-3xl cursor-pointer"
                        onClick={handleDecrement}
                        onPointerDown={(e) => e.stopPropagation()}
                      >
                        <LucideMinus />
                      </div>
                      <div
                        className="p-3 bg-green-400 rounded-3xl cursor-pointer"
                        onClick={handleIncrement}
                        onPointerDown={(e) => e.stopPropagation()}
                      >
                        <LucidePlus />
                      </div>
                    </div>
                  </div>
                </div>
              )) as unknown as React.ReactNode
            }
          </Popup>
          <div onClick={() => useActionStore.getState().removeActionByUid(uid)} onPointerDown={(e) => e.stopPropagation()}>
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer " />
          </div>
        </div>
        <div className="relative ">
          <IconComponent className="w-20 h-20 text-white" />
          <span className="absolute inset-0 flex items-center justify-center text-lg font-semibold text-white">
            {newIterations.toString()}
          </span>
        </div>
        <span className="text-lg font-semibold text-white">Loop Start</span>
      </div>
    );
  }
  
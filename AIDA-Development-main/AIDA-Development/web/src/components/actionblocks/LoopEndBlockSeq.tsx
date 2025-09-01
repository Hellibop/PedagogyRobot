import { LucideX } from "lucide-react";
import { useActionStore } from "../../actionStore";
import { LoopAction } from "../../dataclasses/ActionData";
import { iconMap, IconName } from "../../dataclasses/IconMap";


/*

This is the UI-component to be used as a loop end.
Use in: The sequence. 
Use with: LoopAction (Specifically if isEnd is set to true)


*/

export function LoopEndBlockSeq({
    action,
    closing,
    uid,
  }: {
    action: LoopAction;
    closing: Function;
    uid: number;
  }) {
    const IconComponent = iconMap[action.icon as IconName] 
    return (
      <div className="w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md bg-gray-700 text-center cursor-grab active:cursor-grabbing">
        <div className="relative flex w-full items-center">
          <div onClick={() => useActionStore.getState().removeActionByUid(uid)} onPointerDown={(e) => e.stopPropagation()}>
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
          </div>
        </div>
        <IconComponent className="w-20 h-20 text-white" />
        <span className="text-lg font-semibold text-white">{action.title}</span>
      </div>
    );
  }
  
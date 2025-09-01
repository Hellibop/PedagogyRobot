import { LucideX } from "lucide-react";
import { useActionStore } from "../../actionStore";
import { BaseAction } from "../../dataclasses/ActionData";
import { iconMap, IconName } from "../../dataclasses/IconMap";
import { TimeData } from "../../dataclasses/TimeData";

/* 

This is the UI-component for a BaseAction that sits in the sequence. The main (and only) difference
is that this component shows the time remaining of an action.
Do not use with a special action. Special actions have their own, unqiue UI-components.
I mean if you really want to use it for a special action you can. But why would you?

Use in: The sequence.
Use with: Any Action that is directly a BaseAction and not inheriting BaseAction. So no SpecialAction.

*/

export function ActionBlockSeq({ action, uid }: { action: BaseAction, uid: number }) {
    const IconComponent = iconMap[action.icon as IconName] 
    return (
      <div className="w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md bg-slate-500 cursor-grab active:cursor-grabbing text-center">
        <div className="relative flex w-full items-center">
          <p className="absolute left-1/2 -translate-x-1/2 text-white text-lg font-bold">
            {TimeData[action.title]}
          </p>
          <div onClick={() => useActionStore.getState().removeActionByUid(uid)} onPointerDown={(e) => e.stopPropagation()}>
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
          </div>
        </div>
        <IconComponent className="w-20 h-20 text-white" />
        <span className="text-lg font-semibold text-white">{action.title}</span>
      </div>
    );
  }
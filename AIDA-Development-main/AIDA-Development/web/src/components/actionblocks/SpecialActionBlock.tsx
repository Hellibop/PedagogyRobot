import { BaseAction } from "../../dataclasses/ActionData";
import { iconMap, IconName } from "../../dataclasses/IconMap";

/*

This is the UI-component for the SpecialActions that sit in the footer. 
The idea is that unlike in the sequence where every special action has its own UI-component, that is not needed in the footer.
Therefore we use this for all special actions that sit in the footer. Techincally you can put this in the sequence or use
it with any other normal action. But why would you?

Use in: The footer.
Use with: Any special action. I.e. any action that is NOT directly a BaseAction but rather derives from it. 


*/


export function SpecialActionBlock({ action }: { action: BaseAction }) {
    const IconComponent = iconMap[action.icon as IconName]
    return (
      <div className="w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md bg-gray-700 cursor-grab active:cursor-grabbing text-center">
        <IconComponent className="w-20 h-20 text-white" />
        <span className="text-lg font-semibold text-white overflow-hidden">
          {action.title}
        </span>
      </div>
    );
  }
  
  
  
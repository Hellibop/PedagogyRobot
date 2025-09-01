import { BaseAction } from "../../dataclasses/ActionData";
import { iconMap, IconName } from "../../dataclasses/IconMap";


/*
This is the UI-component used for ActionBlocks in the footer. I.e. these should not be used in the sequence as they
have the incorrect colour and whatnot for that purpose. It is technically possible to use them in the sequence. But why would you?

Use in: The Footer.
Use with: Any Action that is directly a BaseAction and not inheriting BaseAction. So no SpecialAction.

*/

export function ActionBlock({
    action,
    color = "bg-slate-500", //unused
  }: {
    action: BaseAction;
    color?: string;
  }) {
    const IconComponent = iconMap[action.icon as IconName] //Retrieve the icon. 
    
    return (
      <div className="w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 rounded-xl shadow-md bg-blue-300  ${color} text-center">
        <IconComponent className="w-20 h-20 text-white" />
        <span className="text-lg font-semibold text-white overflow-hidden">
          {action.title}
        </span>
      </div>
    );
  }
  
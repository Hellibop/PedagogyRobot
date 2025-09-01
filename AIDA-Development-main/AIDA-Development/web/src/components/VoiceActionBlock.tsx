import { LucidePlus, LucideSettings, LucideX } from "lucide-react"
import { VoiceAction } from "../dataclasses/ActionData"
import { iconMap, IconName } from "../dataclasses/IconMap"
import { useActionStore } from "../actionStore"
import Popup from "reactjs-popup"
import { useState } from "react"



export const VoiceActionBlockSeq = (
    {action, uid}: 
    {action: VoiceAction, uid: number

}) => {

    const IconComponent = iconMap[action.icon as IconName]
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
  overlayStyle={{ zIndex: 9999 }}
  contentStyle={{ zIndex: 10000 }}
>
        {((close: () => void) => {
            const [message, setMessage] = useState(action.message);
            const updateMessage = () => {
            //useActionStore.getState().updateActionMessage(uid, message); 
            close();
            };

            return (
            <div className="bg-slate-700 rounded-lg shadow-lg flex flex-col items-center p-6 relative">
                <div
                onClick={close}
                onPointerDown={(e) => e.stopPropagation()}
                className="absolute right-4 top-4"
                >
                <LucideX className="text-white cursor-pointer" />
                </div>
                <div className="bg-white rounded-lg shadow-lg flex flex-col items-center p-10 m-2">
                <label className="text-black font-semibold mb-2">Edit Message</label>
                <input
                    className="p-2 border rounded w-64"
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                />
                <button
                    className="mt-4 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
                    onClick={updateMessage}
                >
                    Save
                </button>
                </div>
            </div>
            );
        }) as unknown as React.ReactNode}
        </Popup>
          <div onClick={() => useActionStore.getState().removeActionByUid(uid)} onPointerDown={(e) => e.stopPropagation()}>
            <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
          </div>
        </div>
        <IconComponent className="w-20 h-20 text-white" />
        <span className="text-lg font-semibold text-white">{action.title}</span>
      </div>


    );

        
}




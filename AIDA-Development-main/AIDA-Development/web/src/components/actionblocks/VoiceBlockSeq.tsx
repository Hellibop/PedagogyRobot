import { useState, useEffect } from 'react';
import Popup from 'reactjs-popup';
import { LucideSettings, LucideX, LucidePlay } from 'lucide-react';
import { VoiceAction } from '../../dataclasses/ActionData';
import { iconMap, IconName } from '../../dataclasses/IconMap';
import { useActionStore } from '../../actionStore';
import { ActionDataExtended } from '../../dataclasses/ActionDataExtended';
import { inputVoice } from '../../dataclasses/ActionDefinitions';

/*
 * VoiceBlockSeqWorking
 *
 * This component renders a special action block for "Input Voice" actions.
 * A voice action allows the user to type a phrase that will be spoken by the
 * robot.  The block uses a pop‑up to let the user enter or choose a phrase,
 * optionally play the phrase using the browser's text‑to‑speech API, and
 * persist recently used phrases in localStorage.  When no phrase is set
 * the block appears greyed out and displays "EMPTY".
 *
 * Props:
 *   action – the VoiceAction associated with this block
 *   uid – unique identifier for the extended action
 */
export function VoiceBlockSeq({ action, uid }: { action: VoiceAction; uid: number }) {
  // Local state mirrors the current action and its message
  const [currentAction, setCurrentAction] = useState(action);
  const [message, setMessage] = useState(action.message ?? '');

  // Access the updateAction method from the global store
  const updateAction = useActionStore((state) => state.updateAction);

  // Colour for the block: dark when a message is set, light when empty
  const color = message.trim() !== '' ? 'bg-gray-700' : 'bg-neutral-400';

  // Resolve the icon component from the icon map
  const IconComponent = iconMap[action.icon as IconName];

  // Load previously saved phrases from localStorage on mount
  const [savedMessages, setSavedMessages] = useState<string[]>([]);
  useEffect(() => {
    try {
      const raw = localStorage.getItem('voiceMessages');
      if (raw) {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed)) {
          setSavedMessages(parsed);
        }
      }
    } catch {
      setSavedMessages([]);
    }
  }, []);

  /**
   * Persist a phrase to localStorage.  The most recent phrase appears first
   * and the list is limited to 10 entries to avoid excessive storage.
   */
  const persistPhrase = (phrase: string) => {
    const trimmed = phrase.trim();
    if (!trimmed) return;
    const withoutDupes = savedMessages.filter((m) => m !== trimmed);
    const newList = [trimmed, ...withoutDupes].slice(0, 10);
    setSavedMessages(newList);
    localStorage.setItem('voiceMessages', JSON.stringify(newList));
  };

  /**
   * Save handler: update the underlying action with the new message and
   * persist the phrase.  Note that we do not overwrite the action's
   * internalName; the exporter encodes the message separately.
   */
  const handleSave = (close: () => void) => {
    const newVoice = new VoiceAction({ ...currentAction, message });
    const extended = new ActionDataExtended(newVoice);
    extended.uid = uid;
    updateAction(uid, extended);
    setCurrentAction(newVoice);
    persistPhrase(message);
    close();
  };

  /**
   * Reset the action back to an empty Input Voice.  This is invoked when
   * the user presses the "Cancel" button in the pop‑up.
   */
  const handleCancel = (close: () => void) => {
    const emptyVoice = new VoiceAction({ ...inputVoice });
    const extended = new ActionDataExtended(emptyVoice);
    extended.uid = uid;
    updateAction(uid, extended);
    setCurrentAction(emptyVoice);
    setMessage('');
    close();
  };

  /**
   * Speak the current message via the Web Speech API.  If the API is
   * unavailable the function quietly does nothing.  This allows the user
   * to preview how their text will sound when spoken by the robot.
   */
  const handlePlay = () => {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(message);
      window.speechSynthesis.cancel();
      window.speechSynthesis.speak(utterance);
    }
  };

  return (
    <div
      className={`w-40 h-40 flex flex-col items-center justify-center gap-2 p-4 border rounded-xl shadow-md ${color} text-center cursor-grab active:cursor-grabbing`}
    >
      {/* Settings & delete icons */}
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
          {((close: () => void) => (
            <div className="bg-slate-700 rounded-lg shadow-lg flex flex-col items-center p-6 relative">
              <div
                onClick={close}
                onPointerDown={(e) => e.stopPropagation()}
                className="absolute right-4 top-4"
              >
                <LucideX className="text-white cursor-pointer" />
              </div>
              {/* Main pop‑up content */}
              <div className="bg-white rounded-lg shadow-lg flex flex-col items-center p-10 m-2 space-y-4">
                <p className="text-black text-lg font-semibold">Edit Voice Message</p>
                {/* Text input for the message */}
                <input
                  type="text"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  className="p-2 border rounded w-64 text-black"
                  placeholder="Type something for the robot to say"
                />
                {/* Recent phrases */}
                {savedMessages.length > 0 && (
                  <div className="mt-2 w-full max-h-40 overflow-y-auto border rounded p-2 bg-gray-100">
                    <p className="text-sm font-medium mb-1 text-gray-700">Recent phrases:</p>
                    {savedMessages.map((msg, idx) => (
                      <div
                        key={idx}
                        className={`p-1 rounded cursor-pointer ${msg === message ? 'bg-yellow-200' : 'bg-white hover:bg-gray-200'}`}
                        onClick={() => setMessage(msg)}
                      >
                        <span className="text-sm text-gray-800 truncate">{msg}</span>
                      </div>
                    ))}
                  </div>
                )}
                {/* Action buttons */}
                <div className="flex flex-row space-x-4 mt-2">
                  <button
                    onClick={handlePlay}
                    className="flex items-center bg-blue-500 hover:bg-blue-600 text-white px-3 py-2 rounded"
                  >
                    <LucidePlay className="w-4 h-4 mr-1" /> Play
                  </button>
                  <button
                    onClick={() => handleSave(close)}
                    className="flex items-center bg-green-500 hover:bg-green-600 text-white px-3 py-2 rounded"
                  >
                    Save
                  </button>
                  <button
                    onClick={() => handleCancel(close)}
                    className="flex items-center bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-2 rounded"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          )) as unknown as React.ReactNode}
        </Popup>
        {/* Delete icon */}
        <div
          onClick={() => useActionStore.getState().removeActionByUid(uid)}
          onPointerDown={(e) => e.stopPropagation()}
        >
          <LucideX className="absolute right-0 top-1/2 -translate-y-1/2 text-white cursor-pointer" />
        </div>
      </div>
      {/* Main icon */}
      <IconComponent className="w-20 h-20 text-white" />
      {/* Label showing either the message or EMPTY */}
      <span className="text-lg font-semibold text-white overflow-hidden">
        {message.trim() !== '' ? message : 'EMPTY'}
      </span>
    </div>
  );
}
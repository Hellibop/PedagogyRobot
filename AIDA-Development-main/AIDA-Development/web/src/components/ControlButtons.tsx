import { useState } from "react";
import { CircleStop, CirclePlay, CircleChevronRight } from "lucide-react";
import { useActionStore } from "../actionStore";

/**
 * ControlButtons Component
 * 
 * This component renders a set of control buttons (Stop, Play, and Next) that are
 * responsible for controlling the sequence. Each button triggers a specific method 
 * on the sequence controller when clicked. The buttons also provide a press animation 
 * when interacted with.
 *
 * Button Actions:
 * - Stop: Triggers the `stop()` method from the action store.
 * - Play: Triggers the `step()` method from the action store.
 * - Next: Triggers the `play()` method from the action store.
 *
 * Each button has a press animation that scales down the button size slightly when clicked,
 * then returns it to its normal size after 75ms. This provides a visual feedback for user interactions.
 *
 * @returns {JSX.Element} The rendered set of control buttons.
 */
export default function ControlButtons(): JSX.Element {
  // State hooks to manage the "pressed" animation state for each button.
  const [stopPressed, setStopPressed] = useState(false);
  const [playPressed, setPlayPressed] = useState(false);
  const [nextPressed, setNextPressed] = useState(false);

  // Actions from the action store
  const step = useActionStore(state => state.step);
  const stop = useActionStore(state => state.stop);
  const play = useActionStore(state => state.play);

  /**
   * Generic button press handler.
   * 
   * This function handles the press animation and the action execution for a button.
   * When a button is clicked, the press state is set to true to trigger the animation,
   * the respective action is executed, and the pressed state is reset after 75ms.
   *
   * @param {Function} action - A callback function that executes the desired sequence action (stop, step, or play).
   * @param {React.Dispatch<React.SetStateAction<boolean>>} setPressed - The state setter function to toggle the pressed state.
   */
  const handleButtonPress = (
    action: () => void,
    setPressed: React.Dispatch<React.SetStateAction<boolean>>
  ) => {
    // Set the pressed state to true to initiate the animation.
    setPressed(true);

    // Execute the provided action (e.g., stop, step, or play).
    action();

    // After 75ms, reset the pressed state to false to end the animation.
    setTimeout(() => {
      setPressed(false);
    }, 75);
  };

  return (
    <div className="flex flex-col w-40 h-82 size-fit gap-8 p-6 bg-gray-400 rounded-2xl">
      {/* Stop Button */}
      <div
        className="flex items-center justify-center bg-transparent cursor-pointer h-[70px]"
        onClick={() => handleButtonPress(() => stop(), setStopPressed)}
      >
        <div className="flex items-center justify-center w-[70px] h-[70px]">
          <CircleStop
            size={70}
            color="red"
            strokeWidth={1.5}
            className={`hover:opacity-60 transition-transform duration-75 ${stopPressed ? "scale-90" : "scale-100"}`}
          />
        </div>
      </div>

      {/* Next Button (Triggers the play action) */}
      <div
        className="flex items-center justify-center bg-transparent cursor-pointer h-[70px]"
        onClick={() => handleButtonPress(() => play(), setNextPressed)}
      >
        <div className="flex items-center justify-center w-[70px] h-[70px]">
          <CircleChevronRight
            size={70}
            color="yellow"
            strokeWidth={1.5}
            className={`hover:opacity-60 transition-transform duration-75 ${nextPressed ? "scale-90" : "scale-100"}`}
          />
        </div>
      </div>

      {/* Play Button (Triggers the step action) */}
      <div
        className="flex items-center justify-center bg-transparent cursor-pointer h-[70px]"
        onClick={() => handleButtonPress(() => step(), setPlayPressed)}
      >
        <div className="flex items-center justify-center w-[70px] h-[70px]">
          <CirclePlay
            size={70}
            color="lime"
            strokeWidth={1.5}
            className={`hover:opacity-60 transition-transform duration-75 ${playPressed ? "scale-90" : "scale-100"}`}
          />
        </div>
      </div>
    </div>
  );
}

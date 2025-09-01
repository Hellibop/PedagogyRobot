import MovementActionsGrid from "./MovementActionsGrid";
import SpecialActionsGrid from "./SpecialActionsGrid";
import ControlButtons from "./ControlButtons";

import {
  backward,
  forward,
  inputGesture,
  inputSound,
  left,
  longBackwards,
  longForward,
  longLeft,
  longRight,
  loopStart,
  right,
} from "../dataclasses/ActionDefinitions";
import { JSX } from "react";

/**
 * Props for the Footer component.
 * 
 * @property {Function} addBlock - A callback function to add a block to the UI. 
 *                                 Typically used when an action button is clicked.
 */
interface FooterProps {
  addBlock: (block: React.ReactNode) => void;
}

/**
 * Footer Component
 * 
 * The Footer component serves as a control panel UI at the bottom of the screen.
 * It is divided into three areas:
 * 
 * - **Movement Actions Grid**: Contains directional and movement-related action blocks.
 * - **Special Actions Grid**: Contains non-directional or higher-level action blocks like gesture and sound input.
 * - **Control Buttons**: Contains operational controls such as play, pause & reset.
 * 
 * Layout:
 * - The footer uses a horizontal flex layout.
 * - Each section is scrollable and padded for a clean UI.
 * 
 * @param {FooterProps} props - The props for the component.
 * @returns {JSX.Element} A rendered footer with action grids and controls.
 */
export default function Footer({ addBlock }: FooterProps): JSX.Element {
  return (
    <div className="bg-[var(--gray)] h-full flex">
      {/* Movement Actions Panel */}
      <div className="flex-1 min-w-1/3 p-4 overflow-auto">
        <MovementActionsGrid
          addBlock={addBlock}
          actionList={[
            forward,
            longForward,
            backward,
            longBackwards,
            left,
            longLeft,
            right,
            longRight,
          ]}
        />
      </div>

      {/* Special Actions Panel */}
      <div className="w-1/3 p-4 overflow-auto">
        <SpecialActionsGrid
          actionList={[
            inputGesture,
            loopStart,
            inputSound,
          ]}
        />
      </div>

      {/* Control Buttons Panel */}
      <div className="flex p-4 overflow-auto">
        <ControlButtons />
      </div>
    </div>
  );
}

import { JSX } from "react";
import { useActionStore } from "../actionStore";
import { BaseAction } from "../dataclasses/ActionData";
import { ActionBlock } from "./actionblocks/ActionBlock";

/**
 * Props for the Grid component.
 *
 * @property {BaseAction[]} actionList - A list of action definitions to render as blocks.
 * @property {(block: React.ReactNode) => void} addBlock - A callback for adding a block to an external container (unused directly here, but expected from parent).
 */
interface GridProps {
  actionList: BaseAction[];
  addBlock: (block: React.ReactNode) => void;
}

/**
 * Grid Component
 *
 * This component renders a flexible grid of buttons, each containing an `ActionBlock`.
 * When a button is clicked, it adds the associated action to the shared global state via `useActionStore`.
 *
 * Layout:
 * - Uses `flex-wrap` to wrap blocks to new lines as needed.
 * - Adds spacing between blocks with `gap-2`.
 * - Uses `cursor-grab` for drag-style interaction UI (though no actual dragging here, see below). 
 *   TODO:: Make it so that you can drag things from the grid to the sequence bar.
 *
 * @param {GridProps} props - The props including the action list and block handler.
 * @returns {JSX.Element} A rendered grid of action buttons.
 */
export default function Grid({ actionList }: GridProps): JSX.Element {
  return (
    <div className="flex flex-wrap gap-2">
      {actionList.map((action, index) => (
        <button
          key={index}
          className="m-0 p-0px cursor-grab active:cursor-grabbing"
          onClick={() => useActionStore.getState().addAction(action)}
        >
          <ActionBlock action={action} />
        </button>
      ))}
    </div>
  );
}

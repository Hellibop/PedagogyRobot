import { JSX } from "react";
import { useActionStore } from "../actionStore";
import { BaseAction } from "../dataclasses/ActionData";
import { SpecialActionBlock } from "./actionblocks/SpecialActionBlock";

/**
 * Props for the MovementActionsGrid component.
 *
 * @property {BaseAction[]} actionList - A list of movement-related actions to be rendered as buttons.
 */
interface MovementActionsGridProps {
  actionList: BaseAction[];
}

/**
 * MovementActionsGrid Component
 *
 * This component renders a grid of movement action buttons. Each button, when clicked,
 * adds its associated action to the global state via the Zustand store.
 *
 * Layout:
 * - The buttons are arranged in a flexible grid layout with automatic wrapping using `flex-wrap`.
 * - There is a small gap (`gap-2`) between each action button to improve spacing.
 * - Each action is wrapped in a `SpecialActionBlock` for consistent visual styling.
 *
 * @param {MovementActionsGridProps} props - The list of actions to render as buttons.
 * @returns {JSX.Element} The rendered grid of movement action buttons.
 */
export default function MovementActionsGrid({ actionList }: MovementActionsGridProps): JSX.Element {
  return (
    <div className="flex flex-wrap gap-2">
      <div className="flex flex-wrap gap-2">
        {actionList.map((action, index) => (
          <button
            key={index}
            className="m-0 p-0px"
            onClick={() => useActionStore.getState().addAction(action)}
          >
            <SpecialActionBlock action={action} />
          </button>
        ))}
      </div>
    </div>
  );
}

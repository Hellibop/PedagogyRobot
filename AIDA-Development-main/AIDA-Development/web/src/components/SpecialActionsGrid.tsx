import { JSX } from "react";
import { useActionStore } from "../actionStore";
import { BaseAction } from "../dataclasses/ActionData";
import { SpecialActionBlock } from "./actionblocks/SpecialActionBlock";
import { useDraggable } from '@dnd-kit/core';

/**
 * Props for the draggable special actions grid component.
 *
 * @property {BaseAction[]} actionList - A list of special actions to be rendered.
 */
interface SpecialActionsGridProps {
  actionList: BaseAction[];
}

/**
 * SpecialActionsGridComponent
 *
 * This component renders a grid of special action blocks that support both
 * click‑to‑add and drag‑to‑add behaviours. Each block is made draggable
 * using the `useDraggable` hook from @dnd‑kit/core, with a data payload
 * identifying the source as `grid` and containing the corresponding action.
 *
 * When a user clicks a block, the action is appended to the sequence via
 * Zustand's `addAction`. When a block is dragged into the sequence bar,
 * the `App` component's drag end handler will interpret the drag data
 * and insert the action at the appropriate position.  This mirrors the
 * behaviour of the movement actions grid so that special actions can be
 * dragged into the sequence as well.
 *
 * @param {SpecialActionsGridProps} props - The list of actions to display.
 * @returns {JSX.Element} A rendered grid of draggable special actions.
 */
/**
 * Internal component that encapsulates a single draggable special action.
 * It behaves similarly to the movement grid's draggable buttons: a click
 * adds the action to the sequence, while a drag allows the user to drop
 * the action elsewhere.  If the user dragged the item, the click is
 * ignored to prevent adding duplicate actions on drop.
 */
function DraggableSpecialButton({ action, index }: { action: BaseAction; index: number }) {
  const { attributes, listeners, setNodeRef, isDragging } = useDraggable({
    id: `special-grid-${index}`,
    data: { source: 'grid', action },
  });
  const handleClick = () => {
    // Only add the action when not dragging
    if (!isDragging) {
      useActionStore.getState().addAction(action);
    }
  };
  return (
    <div
      ref={setNodeRef}
      {...attributes}
      {...listeners}
      className="cursor-grab"
      style={{ opacity: isDragging ? 0.5 : 1 }}
      onClick={handleClick}
    >
      <SpecialActionBlock action={action} />
    </div>
  );
}

export default function SpecialActionsGrid({ actionList }: SpecialActionsGridProps): JSX.Element {
  return (
    <div className="flex flex-wrap gap-2">
      {actionList.map((action, index) => (
        <DraggableSpecialButton key={index} action={action} index={index} />
      ))}
    </div>
  );
}
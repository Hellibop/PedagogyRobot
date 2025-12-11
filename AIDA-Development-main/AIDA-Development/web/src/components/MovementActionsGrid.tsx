import { JSX } from "react";
import { useActionStore } from "../actionStore";
import { BaseAction } from "../dataclasses/ActionData";
import { ActionBlock } from "./actionblocks/ActionBlock";
import { useDraggable } from '@dnd-kit/core';
import { DndContext } from '@dnd-kit/core';

/**
 * Props for the Grid component.
 *
 * @property {BaseAction[]} actionList - A list of action definitions to render as blocks.
 * @property {(block: React.ReactNode) => void} addBlock - A callback for adding a block to an external container (unused directly here, but expected from parent).
 */
interface GridProps {
  actionList: BaseAction[];
  //addBlock: (block: React.ReactNode) => void;
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
function DraggableButton({ action, index }: { action: BaseAction; index: number }) {
  const { attributes, listeners, setNodeRef, isDragging } = useDraggable({
    id: `grid-movement-${index}`,
    data: { action, source: 'grid' },
  });

  const handleClick = () => {
    // ignore click if user just dragged
    if (!isDragging) useActionStore.getState().addAction(action);
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
      <ActionBlock action={action} />
    </div>
  );
}


export default function MovementActionsGrid({ actionList }: GridProps) {
  return (
    <div className="flex flex-wrap gap-2">
      {actionList.map((action, index) => (
        <DraggableButton key={index} action={action} index={index} />
      ))}
    </div>
  );
}
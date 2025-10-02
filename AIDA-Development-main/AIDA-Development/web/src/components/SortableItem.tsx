import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ActionDataExtended } from '../dataclasses/ActionDataExtended';
import { DynamicActionBlock } from './DynamicActionBlock';

/**
 * Component: SortableItem
 *
 * Wraps an individual action block and makes it sortable via dnd-kit.
 *
 * @param {{ action: ActionDataExtended }} props - The action data, including a unique UID.
 * @returns {JSX.Element} The rendered sortable item with a position badge on top
 */
export function SortableItem({ action, position }: { action: ActionDataExtended, position: number }) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id: action.uid });

  const style = {
    touchAction: 'none', // Prevent default touch actions on draggable items
    transform: CSS.Transform.toString(transform),
    transition,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center'
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners} data-snap-target>
      {/* Position badge */}
      <div className="text-sm font-bold mb-1 bg-neutral-950/60 text-white px-2 py-1 rounded">
        {position}
      </div>
      <DynamicActionBlock action={action.action} uid={action.uid} />
    </div>
  );
}

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
 * @returns {JSX.Element} The rendered sortable item.
 */
export function SortableItem({ action }: { action: ActionDataExtended }) {
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
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners} data-snap-target>
      <DynamicActionBlock action={action.action} uid={action.uid} />
    </div>
  );
}

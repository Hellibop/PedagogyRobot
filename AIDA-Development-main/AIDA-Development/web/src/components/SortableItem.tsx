import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ActionDataExtended } from '../dataclasses/ActionDataExtended';
import { DynamicActionBlock } from './DynamicActionBlock';

/**
 * Component: SortableItem
 *
 * Wraps an individual action block and makes it sortable via dnd-kit.
 *
 * @param {{ action: ActionDataExtended, position: number }} props
 * @returns {JSX.Element} The rendered sortable item with position badge.
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
    touchAction: 'none',
    transform: CSS.Transform.toString(transform),
    transition,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center'
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners} data-snap-target>
      {/* Position badge */}
      <div className="text-sm font-bold mb-1 bg-gray-700 text-white px-2 py-1 rounded">
        {position}
      </div>
      <DynamicActionBlock action={action.action} uid={action.uid} />
    </div>
  );
}
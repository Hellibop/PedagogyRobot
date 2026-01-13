import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ActionDataExtended } from '../dataclasses/ActionDataExtended';
import { DynamicActionBlock } from './DynamicActionBlock';

export function SortableItem({
  action,
  position,
}: {
  action: ActionDataExtended;
  position: number;
}) {
  // Supply custom data so we can tell drags apart in the parent context
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({
    id: action.uid,
    data: { source: 'sequence', action },
  });

  const style = {
    touchAction: 'none',
    transform: CSS.Transform.toString(transform),
    transition,
    display: 'flex',
    flexDirection: 'column' as const,
    alignItems: 'center' as const,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners} data-snap-target>
      <div className="text-sm font-bold mb-1 bg-neutral-950/60 text-white px-2 py-1 rounded">
        {position}
      </div>
      <DynamicActionBlock action={action.action} uid={action.uid} />
    </div>
  );
}

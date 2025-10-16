// SortableItem.tsx — old working version
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ActionDataExtended } from '../dataclasses/ActionDataExtended';
import { DynamicActionBlock } from './DynamicActionBlock';

export function SortableItem({ action, position }: { action: ActionDataExtended, position: number }) {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: action.uid });

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
      <div className="text-sm font-bold mb-1 bg-neutral-950/60 text-white px-2 py-1 rounded">
        {position}
      </div>
      <DynamicActionBlock action={action.action} uid={action.uid} />
    </div>
  );
}

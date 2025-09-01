import { useActionStore } from "../actionStore";

/**
* 
* Component that represents a button to clear the action sequence.
* Using Zustand store clear function.
* 
* @returns {JSX.Element} Rendered ClearButton component. 
*/

export default function ClearButton() {
  return (
    <>
      <div className="p-3 rounded-2xl bg-purple-800 cursor-pointer"
        onClick={() => {
          useActionStore.getState().stop();
           useActionStore.getState().clearAction(); 
           }}>
        <p className="text-white font-bold"> Clear Sequence</p>
      </div>
    </>
  );
}

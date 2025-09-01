import { create } from "zustand";
import { BaseAction, LoopAction, SoundAction, GestureAction } from "./dataclasses/ActionData";
import { arrayMove } from '@dnd-kit/sortable';
import { ActionDataExtended } from "./dataclasses/ActionDataExtended";
import { loopEnd, loopStart } from "./dataclasses/ActionDefinitions";
import { TimeData } from "./dataclasses/TimeData";

// Event handlers for scroll prevention
const preventScroll = (e: Event) => {
  e.preventDefault();
  e.stopPropagation();
};

// Handle mouse down to prevent drag scrolling
const preventMouseDrag = (e: MouseEvent) => {
  e.preventDefault();
  e.stopPropagation();
};

interface ActionStoreState {
  actions: ActionDataExtended[];
  scrollRef: React.RefObject<HTMLDivElement> | null;
  totalTime: number;
  playing: boolean;
  currentIndex: number;
  loopStartIndex: number;
  isInLoop: boolean;
  iterations: number;

  // Actions (methods)
  setScrollRef: (scrollRef: React.RefObject<HTMLDivElement>) => void;
  addAction: (action: BaseAction) => void;
  removeActionByUid: (index: number) => void;
  moveAction: (fromIndex: number, toIndex: number) => void;
  clearAction: () => void;
  updateTime: () => void;
  updateCurrentIndex: (index: number) => void;
  updateAction: (uid: number, updatedAction: ActionDataExtended) => void;

  step: () => void;
  play: () => void;
  stop: () => void;
}
type State = { actions: [], currentindex: 0, totalTime: 0, currentIndex: 0, loopStartIndex: 0, isInLoop: false, iterations: 0 };
const emptyList: State = { actions: [], currentindex: 0, totalTime: 0, currentIndex: 0, loopStartIndex: 0, isInLoop: false, iterations: 0 };
// Create the store
export const useActionStore = create<ActionStoreState>((set, get) => ({
  // Initial state
  actions: [],
  currentIndex: 0,
  totalTime: 0,
  scrollRef: null,
  playing: false,
  loopStartIndex: -1,
  isInLoop: false,
  iterations: -1,

  setScrollRef: (ref: React.RefObject<HTMLDivElement>) => set({ scrollRef: ref }),

  addAction: (action: BaseAction) => {
    set((state) => ({
      playing: false,
    }))
    // If action is a loop action
    if (action instanceof LoopAction) {
      // create two different extendedactions to add bort start and end
      const extendedstart = new ActionDataExtended(loopStart);
      set((state) => ({
        actions: [...state.actions, extendedstart],
      }));
      const extendedend = new ActionDataExtended(loopEnd)
      set((state) => ({
        actions: [...state.actions, extendedend],
      }));


    }
    else {
      const extended = new ActionDataExtended(action);
      set((state) => ({
        actions: [...state.actions, extended],
      }));
    }
    get().stop();
    get().updateTime();
  },

  // Remove an action by index
  removeActionByUid: (uid: number) => {
    set((state) => {
      // Prevent removal while playing
      if (state.playing) {
        console.log("Cannot remove actions while playing");
        return state;
      }
      
      return {
        actions: state.actions.filter((action) => action.uid !== uid)
      };
    });
    get().stop();
    get().updateTime();
  },

  updateAction: (uid: number, updatedAction: ActionDataExtended) => {
    set((state) => {
      // Prevent updates while playing
      if (state.playing) {
        console.log("Cannot update actions while playing");
        return state;
      }
      
      const updatedActions = state.actions.map(action => 
        action.uid === uid ? updatedAction : action
      );
      
      return {
        actions: updatedActions,
        playing: false
      };
    });
    
    get().stop();
    get().updateTime();
  },

  // Reorder an action from one position to another
  moveAction: (fromIndex: number, toIndex: number) =>
    set((state) => {
      if (state.playing) {
        return state; // Do nothing if playing
      }

      return {
        actions: arrayMove(state.actions, fromIndex, toIndex),
      };
    }),


  clearAction: () => {
    set(emptyList);
  },

  updateTime: () =>
    set((state) => {
      let totalTime = 0;
      // for each action in list add it to totalTime
      state.actions.forEach((action) => {

        totalTime += TimeData[action.action.title];
      });
      return { totalTime: totalTime };
    })
  ,
  updateCurrentIndex: (index: number) => {
    set({
      currentIndex: index,
    })
  },

  /*
    Main idea of this function is to handle all types of steps it can make, depending on loops and all. 

    Loop:
    Look if the next action action is a loop, if so go faster and over the loop block.
    if it is a loopStart and the loopstart iteration is more then 1, it should add the loop index and iterations to a Loopstack, making it possible to have nested loops even if we arent gonna have them.
    
    This can be done with a interface to be able to use it nicely. 

    if it is on a loopEnd it should look if there is anything in the loopstack, if it is jump back to it instead of going forward 
    and decrement the iteration, if the iteration is equal to one remove it form the stack.

    
  */
  step: () => {
    set((state) => {
      const scrollContainer = state.scrollRef?.current;

      // 1. Check if we have the scrollRef
      if (!scrollContainer) {
        return state;
      }

      let { playing, currentIndex, actions, isInLoop, loopStartIndex, iterations } = state;

      // console.log("Running step from index: ", currentIndex);

      // Check if we've reached the end of the sequence
      if (currentIndex >= actions.length) {
        console.log("End of sequence reached");
        return { ...state, playing: false };
      }

      const snapTargets = scrollContainer.querySelectorAll('[data-snap-target]');
      
      // Get current action
      const action = actions[currentIndex].action;

      if (currentIndex + 1 < actions.length) {
        console.log("Next item not out of index")
        const nextAction = actions[currentIndex + 1].action;
        if (nextAction instanceof LoopAction && !nextAction.isEnd) {
          console.log("Found loop start on next")
          isInLoop = true;
          iterations = nextAction.iterations;
          loopStartIndex = currentIndex + 1;
          currentIndex += 1;
        }
      }

      // Handle Loop End logic
      if (action instanceof LoopAction && action.title === "Loop End") {
        console.log("Loop End found, iterations remaining:", iterations);
        iterations -= 1;

        if (iterations <= 0) {
          // Loop completed, continue to next action
          isInLoop = false;
          iterations = -1;
          
          // Check if this is the last action
          if (currentIndex === actions.length - 1) {
            console.log("End of sequence reached after loop");
            return { ...state, playing: false, isInLoop, iterations };
          }
        } else {
          // Loop continues, return to loop start
          const loopStartTarget = snapTargets[loopStartIndex + 1] as HTMLElement;
          if (loopStartTarget) {
            const targetScroll = loopStartTarget.offsetLeft + (loopStartTarget.offsetWidth / 2) -
              (scrollContainer.clientWidth / 2);
              
            setTimeout(() => {
              scrollContainer.scrollTo({
                left: targetScroll,
                behavior: 'smooth',
              });
              get().updateCurrentIndex(loopStartIndex + 1);
            }, 1000);
          }
          return { ...state, iterations, isInLoop, loopStartIndex };
        }
      } else if (action instanceof LoopAction && !action.isEnd) {
          isInLoop = true;
          iterations = action.iterations;
          loopStartIndex = currentIndex;
      }

      // Handle scrolling to the next element
      const nextTarget = snapTargets[currentIndex + 1] as HTMLElement;
      let targetScroll = 0;

      if (nextTarget) {
        targetScroll = nextTarget.offsetLeft + (nextTarget.offsetWidth / 2) -
          (scrollContainer.clientWidth / 2);
      } else {
        // No next element, we must be at the end
        console.log("No next element found, stopping");
        return { ...state, playing: false };
      }

      // Scroll to the next element and update current index
      setTimeout(() => {
        scrollContainer.scrollTo({
          left: targetScroll,
          behavior: 'smooth',
        });
        get().updateCurrentIndex(currentIndex + 1);
      }, 1000);

      return { ...state, iterations, isInLoop, loopStartIndex, currentIndex };
    });
  },

  play: () => {
    set((state) => {
      console.log("Playing entire sequence");
      
      // Lock scrolling when play starts
      const scrollContainer = state.scrollRef?.current;
      if (scrollContainer) {
        // Save the original overflow setting
        const originalOverflow = scrollContainer.style.overflow;
        
        // Use CSS to disable scrolling
        scrollContainer.style.overflow = 'hidden';
        
        // Add event listeners to prevent all scrolling interactions
        scrollContainer.addEventListener('wheel', preventScroll, { passive: false, capture: true });
        scrollContainer.addEventListener('touchmove', preventScroll, { passive: false, capture: true });
        scrollContainer.addEventListener('mousedown', preventMouseDrag, { passive: false, capture: true });
        scrollContainer.addEventListener('mousemove', preventMouseDrag, { passive: false, capture: true });
        scrollContainer.addEventListener('keydown', (e) => {
          // Prevent scrolling with arrow keys, spacebar, etc.
          if (['ArrowDown', 'ArrowUp', 'ArrowLeft', 'ArrowRight', ' ', 'Space', 'PageDown', 'PageUp', 'Home', 'End'].includes(e.key)) {
            e.preventDefault();
            e.stopPropagation();
          }
        }, { passive: false, capture: true });
        
        // Store the original overflow state to restore it later
        scrollContainer.dataset.originalOverflow = originalOverflow;
      }
      
      return { ...state, playing: true };
    });

    // Start the recursive stepping function
    const playNextStep = () => {
      const state = get();

      // Stop if no longer playing
      if (!state.playing) {
        console.log("Finished or stopped playing");
        state.stop();
        return;
      }

      // Call step to move to the next item
      get().step();

      // Schedule the next step after a delay
      setTimeout(() => {
        playNextStep();
      }, 2000); // Adjust timing as needed
    };

    // Start the sequence
    playNextStep();
  },
  
  stop: () => {
    console.log("Stopping playback");
    
    // Unlock scrolling when playback stops
    const scrollContainer = get().scrollRef?.current;
    if (scrollContainer) {
      // Remove all event listeners
      scrollContainer.removeEventListener('wheel', preventScroll, { capture: true });
      scrollContainer.removeEventListener('touchmove', preventScroll, { capture: true });
      scrollContainer.removeEventListener('mousedown', preventMouseDrag, { capture: true });
      scrollContainer.removeEventListener('mousemove', preventMouseDrag, { capture: true });
      scrollContainer.removeEventListener('keydown', preventScroll, { capture: true });
      
      // Restore original overflow setting
      const originalOverflow = scrollContainer.dataset.originalOverflow || '';
      scrollContainer.style.overflow = originalOverflow;
      delete scrollContainer.dataset.originalOverflow;
    }
    
    set({ playing: false });
  }
}));

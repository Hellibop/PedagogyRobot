/**
 * Timedata dictionary to be able to look up time by title.
 * Creating this will make easy to switch times later on in project.
 *
 * Note: Added an entry for "Input Voice" so that voice actions are handled
 * consistently in the timeline calculations.  Voice actions use a
 * zero‑second duration by default, meaning they do not advance the
 * timeline when executed unless modified elsewhere.
 */
export const TimeData: { [name: string]: number } = {
    'Left': 2,
    'Long Left': 5,
    'Right': 2,
    'Long Right': 5,
    'Forward': 2,
    'Long Forward': 5,
    'Backwards': 2,
    'Long Backward': 5,
    'Input Gesture': 0,
    'Input Voice': 0,
    'Input Sound': 0,
    'Loop': 0,
    'Loop End': 0,
};
import { LucideArrowDown, LucideArrowUp, LucideArrowLeft, LucideArrowRight, LucideRefreshCcw, LucideHand, LucideMegaphone, LucideMusic2, LucideThumbsUp, LucideThumbsDown, LucidePointer, LucideFingerprint, LucideWaves, LucideStopCircle } from "lucide-react";


/*
    The IconMap is used to map the names of the icons to be used in the actions to the actual icon. 
    This is nesscarry because we can't store actual Icons in localStorage, and we need to do this so the 
    user can save their work when they quit the website. 

    IMPORTANT: If you ever want to change or add an icon. You need to make the changes here!

*/

export const iconMap = {
    LucideArrowDown,
    LucideArrowUp,
    LucideArrowLeft,
    LucideArrowRight,
    LucideRefreshCcw,
    LucideHand,
    LucideMegaphone,
    LucideMusic2,
    LucideThumbsUp,
    LucideThumbsDown,
    LucidePointer,
    LucideFingerprint,
    LucideWaves,
    LucideStopCircle,
    
} as const

export type IconName = keyof typeof iconMap
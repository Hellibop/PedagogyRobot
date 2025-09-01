# AIDA-WEB
This web application is coded in Typescript using React. For most styling TailwindCSS is used. 

## Bugs  
Current known bugs can be found in root of repository docs/bugs

## Installation

Run the command:
```bash
AIDA/web$ npm install
```
Then after installing, to start working run:
```bash
AIDA/web$ npm run dev
```

## Structure 
Following is the important parts of the application. Found in the src folder.
### Components
---
Important components used by the app:
#### ActionBlock.tsx
Different block and sequence block components.  

#### ControlButtons.tsx  
Start, Step, and Stop buttons.
#### MovementActionsGrid.tsx
Component for the footer using actionblocks to create a clickable area.
#### SequenceBar.tsx
Component containing parts of the logic and ui of the sequence bar.
#### SpecialActionsGrid.tsx
Component for the footer using special actionblocks to create a clickable area.


### Dataclasses  
---
Important dataclasses used by the app:
#### ActionData.tsx
Main data class BaseAction derives from here. Used to give all data blocks its initial data.

#### ActionDataExtended.tsx
Wrapper data class for BaseAction.

#### ActionDefinitions.tsx
Filled with definitions of different instances of BaseAction. Enabeling us to use if statements of the instances.

#### Loader.tsx
Used to load in cookies stored in the browser.


### Parser_qr
Components and functions used for the qr-code generation.  
### App.tsx
React root.
### ActionStore.tsx
A store used to update different components async. Dependant on the Zustand package.


--- 
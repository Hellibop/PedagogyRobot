**Bug Report 1**

**Title:** Sequence Plays Incorrect Action or Wiggles if Started During Scroll Animation

**Description:**
When a list of actions is scrolled rapidly (flicked) and playback is initiated while the scroll animation is still active, the playback may begin from the action prior to the one visually centered. This can also cause the UI to "wiggle" back and forth as it tries to correct the focus after jumping to the next action during playback.

**Steps to Reproduce:**
1.  Open a sequence with multiple actions, enough to require scrolling.
2.  Flick the list of actions to scroll it rapidly.
3.  While the scroll animation is still visibly moving, tap the "Play" button.

**Expected Behavior:**
Playback should consistently start from the action that is visually centered (or will be centered once the scroll animation completes) when the play button is pressed. The UI should smoothly transition between actions without wiggling.

**Actual Behavior:**
The sequence sometimes starts playing from the action *before* the one that appears to be current or settling into view. After jumping to the next action during playback, the list might wiggle back and forth.

**Suspected Cause:**
The playback logic might be referencing an index of the "current" item that is captured *before* the scroll animation completes, or there's a race condition between the scroll animation updating the view and the playback engine determining the start index. The wiggling could be the scroll view trying to re-center on one item while playback logic tries to focus on another.

**Severity:** Medium

---

**Bug Report 2**

**Title:** Loop Behavior Ignored When Playback Starts Mid-Loop

**Description:**
If playback of a sequence is initiated from an action that is *inside* a defined loop structure (i.e., not at the "Loop Start" action), the loop's iterative behavior is ignored when the playback reaches the "Loop End" action. Playback continues as if the loop was not present or had already completed its iterations.

**Steps to Reproduce:**
1.  Create a sequence that includes a loop (e.g., "Loop 3 times" action, followed by several actions, then "Loop End").
2.  Manually select an action that is *within* the loop (e.g., the second action inside the loop).
3.  Start playback from this selected action.
4.  Observe the behavior when the playback reaches the "Loop End" action.

**Expected Behavior:**
When starting mid-loop and reaching the "Loop End," the playback should respect the loop's conditions so it should jump back to the "Loop Start" action and continue iterating until the count is met.

**Actual Behavior:**
When playback reaches the "Loop End" action after starting mid-loop, the loop's conditions are ignored. Playback proceeds to the action immediately following the "Loop End," as if the loop was not active or had already completed all its iterations.

**Suspected Cause:**
When playing and a loop start is encountered, it's index is stored in a local variable, and when reaching a loop end, that variable is simply jumped to. 


**Severity:** Medium


---

**Bug Report 3**

**Title:** Grant Permissions Popup Unresponsive After Initial QR Permission Denial

**Description:**
If a user denies camera permissions for QR code scanning when first prompted by the OS, a subsequent in-app popup or button intended to allow the user to grant these permissions does not function. Clicking it does not re-trigger the OS permission dialog or navigate to app settings.

**Steps to Reproduce:**
1.  Launch the app for the first time or after clearing app data/permissions.
2.  Attempt to use the QR code scanning feature.
3.  When the OS permission dialog for camera access appears, select "Deny."
4.  The app may then display its own UI element (e.g., a popup or button) stating permissions are needed and offering a way to grant them.
5.  Click this in-app "Grant Permissions" element.

**Expected Behavior:**
Clicking the in-app "Grant Permissions" element should either:
a. Re-trigger the OS camera permission dialog.
b. Or, more commonly for subsequent denials, direct the user to the app's specific settings page within the OS settings, where they can manually enable the camera permission.

**Actual Behavior:**
Clicking the in-app "Grant Permissions" element has no effect. The OS permission dialog does not reappear, and the user is not guided to settings.

**Suspected Cause:**
The app might not be correctly handling the "permanently denied" (or "denied once") permission state. It might only be coded to request permissions initially and lacks the logic to direct users to system settings for changes after a denial, or it's not re-querying the permission status correctly.

**Severity:** Medium

---

**Bug Report 4**

**Title:** User Not Notified of Disconnection Until Attempting Server Interaction

**Description:**
If the application loses its connection to the server (e.g., due to network issues), the user is not proactively informed of this disconnection. The loss of connection only becomes apparent when the user attempts an action that requires server communication, which then may fail or hang.

**Steps to Reproduce:**
1.  Ensure the application is connected to the server and functioning normally.
2.  Simulate a loss of network connectivity (e.g., turn off Wi-Fi/mobile data on the device, or disconnect the server).
3.  Observe the application; no immediate notification of disconnection is shown.
4.  Attempt an action that requires server interaction (e.g., saving data, loading a new sequence, fetching updates).

**Expected Behavior:**
The application should detect the loss of server connectivity in a timely manner and provide a clear visual indicator or notification to the user.

**Actual Behavior:**
The application provides no immediate feedback about the lost connection. The user remains unaware until they try to interact with a server-dependent feature.

**Suspected Cause:**
The app only checks connectivity when a server request is made.

**Severity:** Medium

---

**Bug Report 5**

**Title:** Potential Crash or Undefined Behavior When Playing Special Actions with No Data

**Description:**
Attempting to play a special action (e.g., a sound action, gesture action) that has not been configured with the necessary data (e.g., no sound file selected, no gesture defined) may lead to application instability, such as a crash, an unhandled error, or other unpredictable behavior.

**Steps to Reproduce:**
1.  Create a new sequence or edit an existing one.
2.  Add a special action that requires data (e.g., "Play Sound" action or "Perform Gesture" action).
3.  Do *not* assign any specific data to this action (e.g., leave the sound file selection empty, or don't define a gesture).
4.  Attempt to play the sequence, specifically ensuring this unconfigured action is executed.

**Expected Behavior:**
The application should gracefully handle special actions with missing data. Options include:
a. Silently skipping the action.
b. Displaying a user-friendly message (e.g., "No sound selected for action X").
c. Playing a default placeholder sound/gesture if applicable.
The application should not crash or enter an error state.

**Actual Behavior:**
It has not been tested what behaviour this causes.

**Suspected Cause:**
Missing null checks or error handling within the playback engine when it attempts to access or process the data associated with a special action.

**Severity:** High (if it leads to crashes)

---

**Bug Report 6**

**Title:** Inconsistent Theme Application and Awkward Colors in Dark Mode

**Description:**
The application's visual theme, especially in dark mode, is not consistently applied across all UI elements. Some components or screens may retain light mode styling or use colors that have poor contrast, appear out of place, or are aesthetically unpleasing in the dark mode context.

**Steps to Reproduce:**
1.  Enable Dark Mode in the application's settings or ensure the OS is in Dark Mode (if the app follows system settings).
2.  Navigate through various screens, popups, lists, and interact with different UI elements (buttons, text fields, etc.).
3.  Observe the colors of backgrounds, text, icons, and other components.

**Expected Behavior:**
All UI elements should consistently adhere to the selected theme. In dark mode, this typically means dark backgrounds, light text, and accent colors that are harmonious and provide good readability and visual appeal.

**Actual Behavior:**
Some UI elements do not correctly adopt the dark mode theme. They might display light backgrounds, dark text on dark backgrounds, or use colors that clash with the dark theme, making them look "strange" or difficult to read.

**Suspected Cause:**
a. Hardcoded color values in some UI components instead of using theme variables.
b. Incomplete or overridden styles for certain elements in the dark mode theme definition.
c. Use of default system component styles that don't automatically adapt or are not styled for dark mode.

**Severity:** Low (primarily a UI/UX and accessibility issue, but can significantly impact user experience)

---

**Bug Report 7**

**Title:** Sounds Can Be Played Overlappingly by Multiple Clicks in Sound Popup

**Description:**
Within the sound selection popup, clicking on a sound item multiple times in quick succession causes the sound to play multiple instances of itself simultaneously. This results in an overlapping audio output.

**Steps to Reproduce:**
1.  Open the sound selection popup (e.g., when adding or editing a "Play Sound" action).
2.  Locate any sound item in the list that can be previewed by clicking.
3.  Click the sound item rapidly two or more times.

**Expected Behavior:**
When a sound item is clicked in the preview list:
a. If it's not playing, it should start playing.
b. If it's already playing, clicking it again should ideally either restart the sound from the beginning or do nothing (preventing overlap). A single instance of the preview sound should play at any given time.

**Actual Behavior:**
Each click on a sound item initiates a new, independent playback of that sound. These playbacks occur concurrently, leading to multiple instances of the same sound playing over each other.

**Suspected Cause:**
The event handler for the sound item click creates and starts a new audio player instance on every click, without checking for or stopping any existing playback instances of that same sound preview.

**Severity:** Medium (can be annoying and loud)

---

**Bug Report 8**

**Title:** Playing Sounds Not Cancelled When Sound Popup is Closed

**Description:**
If one or more sounds are actively playing as previews within the sound selection popup, closing this popup does not stop the audio playback. The sound(s) continue to play in the background until they naturally conclude.

**Steps to Reproduce:**
1.  Open the sound selection popup.
2.  Click one or more sound items to start their playback (as described in Bug Report 7, they might overlap).
3.  While the sound(s) are still playing, close the sound popup.

**Expected Behavior:**
Any sound playback initiated from within the sound selection popup should be immediately stopped or cancelled when the popup is closed or dismissed.

**Actual Behavior:**
The sound(s) that were playing when the popup was closed continue to play in the background until they finish their natural duration.

**Suspected Cause:**
The lifecycle management of the sound popup does not include logic to stop active audio players or streams when it is dismissed or hidden. The audio playback instances are not tied to the visibility or existence of the popup UI.

**Severity:** Low

---

**Bug Report 9**

**Title:** Data for Special Actions Not Imported via QR Code Scan

**Description:**
When a sequence containing special actions (e.g., a gesture action with a specific gesture selected, or a sound action with a specific sound file chosen) is exported to a QR code and then imported by scanning that QR code, the special actions themselves appear in the imported sequence, but their associated data (the specific gesture, sound file, etc.) is missing.

**Steps to Reproduce:**
1.  Create a sequence.
2.  Add a special action that requires specific data (e.g., "Play Sound").
3.  Configure this action with specific data.
4.  Export this sequence to a QR code.
5.  Use the app to scan this QR code and import the sequence.
6.  Inspect the imported sequence, particularly the special action.

**Expected Behavior:**
The imported special action should retain all its configured data.

**Actual Behavior:**
The special action (e.g., "Play Sound") is present in the imported sequence, but its specific data is missing.

**Suspected Cause:**
The serialization process for generating the QR code data might not be correctly encoding the specific data associated with these special actions, or it might be encoding it in a way that the deserialization/import process cannot correctly interpret.

**Severity:** High (significantly impairs the utility of QR code sharing for sequences with special actions)

---

**Bug Report 10**

**Title:** Lock Button Fails to Prevent Deletion of Individual Actions

**Description:**
Activating the "Lock" feature for a sequence, which is intended to prevent accidental modifications, does not prevent the user from deleting individual actions within that locked sequence.

**Steps to Reproduce:**
1.  Open or create a sequence one or more actions.
2.  Locate and activate the "Lock" button or toggle for the sequence.
3.  With the sequence now in a "locked" state, attempt to delete an individual action from the list using the icon in it's corner.

**Expected Behavior:**
When a sequence is locked, all operations that modify the sequence, including the deletion of individual actions, should be disabled or prevented.

**Actual Behavior:**
Even when the sequence is "locked," individual actions can still be deleted from it using the standard methods.

**Suspected Cause:**
The "Lock" state check is likely not implemented for deleting action's via their delete icon.

**Severity:** Medium (the lock feature does not provide the intended level of protection against accidental changes)

---
package com.example.aida.ui.page

import android.content.ClipDescription
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.component.ActionButton
import com.example.aida.ui.component.ClearSequenceButton
import com.example.aida.ui.component.ImportButton
import com.example.aida.ui.component.LockButton
import com.example.aida.ui.component.ProgressBar
import com.example.aida.ui.component.SequenceBar
import com.example.aida.ui.component.TimeLine
import com.example.aida.ui.component.UserButtons
import com.example.aida.ui.constants.progressBarWidth
import com.example.aida.ui.constants.sequenceBarActionDistance
import com.example.aida.ui.constants.sequenceBarActionSnapThreshold
import com.example.aida.ui.constants.sequenceBarActionWidth
import com.example.aida.ui.constants.sequenceTabDraggingColor
import com.example.aida.ui.constants.sequenceTabIdleColor
import com.example.aida.ui.constants.sequenceTabPlayingColor
import com.example.aida.ui.viewmodel.SequenceViewModel
import com.example.aida.ui.viewmodel.UserInteractionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Displays the entire page for creating sequences of actions and then playing them.
 *
 * @param viewModel Stores the state for the sequence tab. Automatically injected via Hilt.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SequenceTabPage(
    viewModel: SequenceViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.sequenceBarState.collectAsState()

    // Colors representing various states
    // Scroll state to handle horizontal scrolling of the sequence bar
    // Save previous state in order to detect when going from scrolling to not scrolling
    val scrollState = rememberScrollState()
    var lastScrollState by remember { mutableStateOf(scrollState.isScrollInProgress) }

    /* Precalculate pixel values since conversion between dp and int cant happen
       in non-composable functions */
    val stepDistanceInPixels = dpToInt(sequenceBarActionDistance)
    val actionRemainderInPixels = getActionRemainder(scrollState.value).toFloat()
    val actionWidthInPixels = dpToInt(sequenceBarActionWidth)
    val actionDistanceInPixels = dpToInt(sequenceBarActionDistance)

    var currentIndex = currentActionIndex(scrollState.value)

    // The currentlly active coroutine that plays a sequence. Storing it is needed
    // since we need to cancel the animation and countdown when pressing stop.
    var currentPlayJob: Job? = null

    // Lambda that gets called when scrolling is stopped, snaps to closest action
    val snapToClosestAction: () -> Unit = {
        coroutineScope.launch {
            // If scrolled past half the action size, snap to next action.
            // Otherwise, snap to beginning of current action.
            val snapThresholdInPixels = actionWidthInPixels.toFloat() * sequenceBarActionSnapThreshold
            val snapToNext = actionRemainderInPixels > snapThresholdInPixels
            val maxScrollValue = (viewModel.actionCount()) * actionDistanceInPixels
            val stepAmount =
                if (snapToNext) (stepDistanceInPixels - actionRemainderInPixels) else -actionRemainderInPixels

            // Clamp scroll to be at most 1 past end of action list in order to avoid scrolling bug.
            // TODO: handle this in a better way?
            val nextScrollValue =
                (scrollState.value + stepAmount).coerceAtMost(maxScrollValue.toFloat())
            scrollState.animateScrollTo(nextScrollValue.toInt())
        }
    }

    // Gets called when dragging an action from the actions library.
    // Used to handle state changes and adding actions to bar.
    val dragAndDropCallback = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                viewModel.setState(UserInteractionState.DRAGGING)
            }

            override fun onEnded(event: DragAndDropEvent) {
                viewModel.setState(UserInteractionState.STOPPED)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                // Retrieve the text field from the event in a very convenient way
                val type = event.toAndroidDragEvent().clipData.getItemAt(0).text

                // Try converting the string into an action enum
                val actionEnum = RobotActionType.entries.firstOrNull { it.name == type }

                if (actionEnum == null) {
                    // The action text was somehow invalid.
                    return false
                }

                addActionToSequenceBar(
                    actionEnum,
                    scrollState,
                    coroutineScope,
                    viewModel,
                    stepDistanceInPixels,
                    snapToClosestAction
                )

                return true
            }
        }
    }



    // Watch for the scroll state change
    LaunchedEffect(scrollState.isScrollInProgress) {
        // Check if the state changed from true to false
        if (lastScrollState && !scrollState.isScrollInProgress) {
            snapToClosestAction()
        }
        // Update the remembered state for the next check
        lastScrollState = scrollState.isScrollInProgress
    }

    // Watch for sequence bar state change
    LaunchedEffect(uiState.menuState) {
        // Reset all durations when we stop playing
        if (uiState.menuState == UserInteractionState.STOPPED) {
            currentPlayJob?.cancelAndJoin()
            viewModel.resetAllDurations()
        }
    }

    // SEQUENCE BAR
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
                                uiState.menuState == UserInteractionState.STOPPED &&
                                !uiState.isLocked
                    },
                    target = dragAndDropCallback
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = getColorForState(uiState.menuState)),
                contentAlignment = Alignment.Center
            ) {

                // Button to clear all actions from the sequence bar
                ClearSequenceButton(
                    viewModel,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 10.dp, y = 10.dp)
                        .size(135.dp, 45.dp)
                        .zIndex(2f),
                    snapToClosestAction = snapToClosestAction
                )

                //Button to invoke the QR-scanner to import code from the webapp.
                ImportButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                        .size(135.dp, 45.dp)
                        .zIndex(2f),
                    viewModel = viewModel
                )

                LockButton(
                    viewModel = viewModel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-150).dp, y = 10.dp)
                        .size(45.dp, 45.dp)
                        .zIndex(2f),
                    uiState = uiState
                )

                // Timeline (vertical line) on the left side to indicate the starting point
                TimeLine()

                // Progress bar at the bottom to show scrolling position relative to total width
                ProgressBar(
                    scrollState = scrollState,
                    totalWidth = progressBarWidth,
                    uiState.actions.size,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .zIndex(2f),
                    onDragStopped = snapToClosestAction
                )

                // Reorderable list of actions
                SequenceBar(
                    coroutineScope = coroutineScope,
                    viewModel = viewModel,
                    uiState = uiState,
                    scrollState = scrollState,
                    snapToClosestAction = snapToClosestAction
                )
            }
        }

        // ACTION LIBRARY AND USER BUTTONS SECTION
        Row(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.LightGray)
                .fillMaxSize()
        ) {
            // ACTION LIBRARY: A grid of actions that can be dragged into the sequence bar
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .width(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Display the action library as a grid of draggable buttons
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(175.dp)
                )
                {
                    // Grid for normal actions
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, top = 20.dp, end = 8.dp, bottom = 8.dp)
                            .fillMaxHeight()
                    ) {
                        items(
                            items = RobotActionType.entries.filter { !it.isSpecial },
                            itemContent = { actionType ->
                                ActionButton(
                                    actionType = actionType,
                                    uiState = uiState,
                                    onClick = {
                                        if (!uiState.isLocked) {
                                            // Click directly adds the action to the sequence bar
                                            addActionToSequenceBar(
                                                actionType,
                                                scrollState,
                                                coroutineScope,
                                                viewModel,
                                                stepDistanceInPixels,
                                                snapToClosestAction
                                            )
                                        }
                                    },
                                )
                            }
                        )
                    }

                    // Grid for special actions
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(start = 8.dp, top = 20.dp, end = 8.dp, bottom = 8.dp)
                            .fillMaxHeight()
                    ) {
                        items(
                            items = RobotActionType.entries.filter { it.isSpecial && it != RobotActionType.LOOP_END },
                            itemContent = { actionType ->
                                ActionButton(
                                    actionType = actionType,
                                    uiState = uiState,
                                    onClick = {
                                        // Click directly adds the action to the sequence bar
                                        if (!uiState.isLocked) {
                                            addActionToSequenceBar(
                                                actionType,
                                                scrollState,
                                                coroutineScope,
                                                viewModel,
                                                stepDistanceInPixels,
                                                snapToClosestAction
                                            )
                                        }
                                    },
                                )
                            }
                        )
                    }
                }
            }

            // USER BUTTONS: Play, stop, step, etc.
            UserButtons(
                onClickPlay = {
                    viewModel.setState(UserInteractionState.PLAYING)
                    // TODO: avoid this code duplication
                    currentPlayJob = coroutineScope.launch {
                        if (currentIndex == uiState.actions.size) {
                            // Scroll to beginning if we are on the last block
                            coroutineScope.launch {
                                animateScrollToIndex(
                                    0,
                                    scrollState,
                                    stepDistanceInPixels,
                                    tween(durationMillis = 750, easing = FastOutSlowInEasing)
                                )

                                currentIndex = 0
                            }.join()
                        }

                        var loopStartIndex = -1 // Set to -1 to indicate that we're not in a loop
                        var loopIterationsRemaining = 0

                        snapToClosestAction()

                        while ((currentIndex < uiState.actions.size) &&
                            (uiState.menuState == UserInteractionState.PLAYING)
                        ) {
                            snapToClosestAction()

                            val currentAction = uiState.actions[currentIndex]

                            // Don't update loop start if we already have encountered this loop start
                            if ((currentAction.action.type == RobotActionType.LOOP_START) && (loopStartIndex != currentIndex)) {
                                loopStartIndex = currentIndex
                                loopIterationsRemaining = currentAction.iterations
                            } else if (currentAction.action.type == RobotActionType.LOOP_END) {
                                if (loopIterationsRemaining > 1) {
                                    loopIterationsRemaining--
                                    viewModel.resetDurationsInRange(
                                        loopStartIndex,
                                        currentIndex
                                    )

                                    currentIndex = loopStartIndex - 1 // Jump to loop start action
                                }
                            }

                            val task = launch {
                                executeActionWithCountdown(
                                    currentIndex,
                                    coroutineScope,
                                    viewModel,
                                    scrollState,
                                    stepDistanceInPixels,
                                )
                            }

                            task.join()

                            currentIndex++
                        }

                        viewModel.setState(UserInteractionState.STOPPED)
                    }
                },
                onClickStep = {
                    // TODO: avoid this code duplication
                    if (uiState.menuState == UserInteractionState.STOPPED) {
                        currentPlayJob = coroutineScope.launch {
                            viewModel.setState(UserInteractionState.PLAYING)
                            snapToClosestAction()

                            // Scroll to beginning if we are on the last block
                            if (currentIndex == uiState.actions.size) {
                                coroutineScope.launch {
                                    animateScrollToIndex(
                                        0,
                                        scrollState,
                                        stepDistanceInPixels,
                                        tween(durationMillis = 750, easing = FastOutSlowInEasing)
                                    )

                                    currentIndex = 0
                                }.join()
                            }

                            val task = launch {
                                executeActionWithCountdown(
                                    currentIndex,
                                    coroutineScope,
                                    viewModel,
                                    scrollState,
                                    stepDistanceInPixels,
                                )
                            }

                            // Wait for animation to finish
                            task.join()
                            viewModel.setState(UserInteractionState.STOPPED)
                        }
                    }
                },
                onClickStop = {
                    if (uiState.menuState == UserInteractionState.PLAYING) {
                        coroutineScope.launch {
                            //viewModel.stopExecutingActions()
                        }
                    } else {
                        // Go to beginning
                        coroutineScope.launch {
                            animateScrollToIndex(
                                0,
                                scrollState,
                                stepDistanceInPixels,
                                tween(durationMillis = 750, easing = FastOutSlowInEasing)
                            )
                        }
                    }

                    viewModel.setState(UserInteractionState.STOPPED)
                },
                uiState = uiState
            )
        }
    }
}

/**
 * Executes an action then animates scrolling to the next action in the sequence.
 *
 * @param index The index of the action to execute.
 * @param coroutineScope A scope to launch scrolling animations.
 * @param viewModel The state of the sequence bar UI.
 * @param scrollState The scroll state of the sequence bar.
 * @param stepLength The distance in pixels that each step should be.
 */
private suspend fun executeActionWithCountdown(
    index: Int,
    coroutineScope: CoroutineScope,
    viewModel: SequenceViewModel,
    scrollState: ScrollState,
    stepLength: Int,
) {
    val task = coroutineScope.launch {
        // TODO: handle this in a better way, without an if check on index
        // TODO: we shouldn't send actions if they're loop actions
        if (index >= 0) {
            viewModel.executeAction(index)

            while ((viewModel.getAction(index).durationRemaining > 0) && (viewModel.getState() != UserInteractionState.STOPPED)) {
                val action = viewModel.getAction(index)
                delay(100)

                if (viewModel.getState() != UserInteractionState.STOPPED) {
                    viewModel.setDuration(index, (action.durationRemaining - 0.1))
                }
            }
        }

        if (viewModel.getState() != UserInteractionState.STOPPED) {
            animateScrollToIndex(
                index + 1,
                scrollState,
                stepLength,
                tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    task.join()
}

/**
 * Executes an action then animates scrolling to the next action in the sequence.
 * (there is no builtin way to do this for some reason)
 *
 * @param index The index of the action to execute.
 * @param scrollState The scroll state of the sequence bar.
 * @param stepDistanceInPixels How long in pixels each step between actions is.
 * @param animationSpec Used to customize the appearance of the animation.
 */
private suspend fun animateScrollToIndex(
    index: Int,
    scrollState: ScrollState,
    stepDistanceInPixels: Int,
    animationSpec: AnimationSpec<Float>? = null,
) {
    val scrollValue = stepDistanceInPixels * index

    if (animationSpec != null) {
        scrollState.animateScrollTo(scrollValue, animationSpec)
    } else {
        scrollState.animateScrollTo(scrollValue)
    }
}

/**
 * Calculates the currently centered action based on scroll value.
 * (there is no builtin way to do this for some reason)
 *
 * @param scrollValue The scroll value, retrieved from the ScrollState.
 */
@Composable
private fun currentActionIndex(
    scrollValue: Int
): Int {
    return (scrollValue / (dpToInt(sequenceBarActionDistance)))
}

/**
 * Calculates how much far we are from being aligned to the center action's left border.
 * This is used for snapping to the closest action
 *
 * @param scrollValue The scroll value, retrieved from the ScrollState.
 */
@Composable
private fun getActionRemainder(
    scrollValue: Int
): Int {
    return (scrollValue) % (dpToInt(sequenceBarActionDistance))
}

/**
 * Converts a pixel-independent value to an integer, based on the current resolution.
 *
 * @param dpValue The dp value to convert.
 */
@Composable
private fun dpToInt(dpValue: Dp): Int {
    return with(LocalDensity.current) { dpValue.toPx().roundToInt() }
}

/**
 * Adds an action to the sequence bar.
 *
 * If the action is "LOOP", it actually adds a LOOP START and LOOP END pair.
 * Otherwise, it copies the action's current state and adds it.
 *
 * @param action The action to add.
 * @param scrollState The scroll state of the sequence bar.
 * @param coroutineScope A scope to launch scrolling animations if needed.
 * @param viewModel The state of the sequence bar UI.
 * @param stepDistanceInPixels How long in pixels each step between actions is.
 * @param snapToClosestAction A lambda function that snaps the scroll value to the closest action.
 */
private fun addActionToSequenceBar(
    action: RobotActionType,
    scrollState: ScrollState,
    coroutineScope: CoroutineScope,
    viewModel: SequenceViewModel,
    stepDistanceInPixels: Int,
    snapToClosestAction: () -> Unit
) {
    if (viewModel.getState() == UserInteractionState.PLAYING)
        return

    viewModel.addAction(action)

    snapToClosestAction()

    // If many actions are present, scroll to the end when adding a new one
    if (viewModel.actionCount() >= 5) {
        coroutineScope.launch {
            animateScrollToIndex(
                viewModel.actionCount() - 1,
                scrollState,
                stepDistanceInPixels,
                tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
        }
    }
}

/**
 * Returns what the background color should currently be based on the UI state.
 *
 * This is used to make the background gray when playing a sequence.
 *
 * @param state An enum that describes what user interaction state we are currently in.
 */
private fun getColorForState(
    state: UserInteractionState
): Color {
    return when (state) {
        UserInteractionState.STOPPED -> sequenceTabIdleColor
        UserInteractionState.DRAGGING -> sequenceTabDraggingColor
        UserInteractionState.PLAYING -> sequenceTabPlayingColor
    }
}
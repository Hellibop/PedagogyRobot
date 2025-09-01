package com.example.aida.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.constants.actionFontsize
import com.example.aida.ui.popups.PopupGestures
import com.example.aida.ui.popups.PopupLoop
import com.example.aida.ui.popups.PopupSounds
import com.example.aida.ui.popups.PopupVoices
import com.example.aida.ui.constants.sequenceBarActionDistance
import com.example.aida.ui.constants.sequenceBarActionHeight
import com.example.aida.ui.constants.sequenceBarActionPadding
import com.example.aida.ui.constants.sequenceBarActionWidth
import com.example.aida.ui.constants.specialActionTextLimit
import com.example.aida.ui.viewmodel.SequenceBarState
import com.example.aida.ui.viewmodel.SequenceViewModel
import com.example.aida.ui.viewmodel.UserInteractionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableRow
import java.util.Locale


/**
 * Displays a horizontal "sequence bar" where users can arrange and configure actions.
 *
 * This composable shows a row of actions that can be reordered,
 * edited via double-tap or a settings button, and removed. It also includes a scrollable timeline
 * and a progress bar to indicate the current position when scrolling horizontally.
 *
 * @param viewModel The [SequenceViewModel] that contains business logic functions.
 * @param scrollState The [ScrollState] controlling horizontal scrolling of the bar.
 * @param coroutineScope The [CoroutineScope] used to launch animations or asynchronous tasks.
 * @param uiState The [SequenceBarState] that contains the state for the SequenceBar such as
 *  the contained actions.
 * @param snapToClosestAction A lambda function that snaps to the action closest to tbe center.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SequenceBar(
    viewModel: SequenceViewModel,
    coroutineScope: CoroutineScope,
    uiState: SequenceBarState,
    scrollState: ScrollState,
    snapToClosestAction: () -> Unit,
) {
    // Provides access to the haptic feedback system
    val haptic = LocalHapticFeedback.current

    // Tracks interaction states for UI components
    val interactionSource = remember { MutableInteractionSource() }

    // TODO: create function to get screen dimensions
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    data class PopupState(
        var shouldShowPopup: MutableState<Boolean> = mutableStateOf(false),
        var selecetedIndex: MutableState<Int> = mutableStateOf(0)
    ) {
        fun activate(index: Int) {
            if (!uiState.isLocked) {
                shouldShowPopup.value = true
                selecetedIndex.value = index
            }
        }

        fun deactivate() {
            shouldShowPopup.value = false
        }
    }

    val popupState = remember { mutableStateOf(PopupState()) }

    val onDismiss: () -> Unit = {
        popupState.value.deactivate()
    }

    if (popupState.value.shouldShowPopup.value && uiState.menuState == UserInteractionState.STOPPED) {
        val actionType = viewModel.getAction(popupState.value.selecetedIndex.value).action.type

        when (actionType) {
            RobotActionType.LOOP_START -> {
                PopupLoop(
                    onDismiss = onDismiss,
                    onSave = { value ->
                        onDismiss()
                        viewModel.setIterations(popupState.value.selecetedIndex.value, value)
                    }
                )
            }

            RobotActionType.INPUT_SOUND -> {
                PopupSounds(
                    onDismiss = onDismiss,
                    onSave = { sound ->
                        onDismiss()
                        viewModel.setData(popupState.value.selecetedIndex.value, sound)
                    }
                )
            }

            RobotActionType.INPUT_GESTURE -> {
                PopupGestures(
                    onDismiss = onDismiss,
                    onSave = { name ->
                        onDismiss()
                        viewModel.setData(popupState.value.selecetedIndex.value, name)
                    }
                )
            }

            RobotActionType.INPUT_VOICE -> {
                PopupVoices(
                    onDismiss = onDismiss,
                    onSave = { text ->
                        onDismiss()
                        viewModel.setData(popupState.value.selecetedIndex.value, text)
                    },
                    onPlay = { _, _->

                    },
                    context = LocalContext.current
                )
            }

            else -> { }
        }

    }

    // Reorderable row of actions
    ReorderableRow(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .horizontalScroll(
                scrollState,
                enabled = (uiState.menuState == UserInteractionState.STOPPED)
            )

            // Padding creates space to scroll and position items further right.
            .padding(
                PaddingValues(
                    start = screenWidth / 2 - sequenceBarActionWidth / 2,
                    top = 8.dp,
                    end = screenWidth / 2 + sequenceBarActionDistance,
                    bottom = 8.dp
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sequenceBarActionPadding),
        list = uiState.actions,
        onSettle = { fromIndex, toIndex ->
            viewModel.moveAction(fromIndex, toIndex)
        },
        onMove = {
            // Provide haptic feedback when dragging starts
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        },
    ) { index, item, _ ->
        key(item) {
            // Each action is represented as a card:
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Card(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = tween(durationMillis = 250, easing = EaseInOut),
                            alignment = Alignment.Center
                        )
                        .width(sequenceBarActionWidth)
                        .height(sequenceBarActionHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GetActionColor(item.action))
                        .combinedClickable(onDoubleClick = {
                            // Activate popup when double clicking
                            popupState.value.activate(index)
                        }) { }
                        .semantics {
                            // Accessibility custom actions for moving items left or right
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = "Move Left",
                                    action = {
                                        if (index > 0) {
                                            viewModel.moveAction(index, index - 1)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                                CustomAccessibilityAction(
                                    label = "Move Right",
                                    action = {
                                        if (index < viewModel.actionCount() - 1) {
                                            viewModel.moveAction(index, index + 1)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                            )
                        }
                        .then(
                            // Allow dragging actions if we are not playing
                            if (uiState.menuState != UserInteractionState.PLAYING && !uiState.isLocked) {
                                Modifier.draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragStopped = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    interactionSource = interactionSource
                                )
                            } else {
                                Modifier
                            }
                        ),

                    colors = CardDefaults.cardColors(containerColor = GetActionColor(item.action))
                ) {
                    // Main container inside an action block
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Different layouts for special cases like LOOP START/END

                        if (item.action.type.isSpecial) {
                            // Display the special action icon and data (e.g., a gesture name)
                            renderSpecialActionButton(item)

                            // If this is a special action (except LOOP END), show a settings button to open popup
                            if (item.action.type != RobotActionType.LOOP_END) {
                                IconButton(
                                    onClick = {
                                        popupState.value.activate(index)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                        .size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Bold Close",
                                        tint = Color.White,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        } else {
                            // Default case for normal actions: show icon and name
                            renderNormalActionButton(item)
                        }

                        // Close (X) button to remove the action from the bar
                        IconButton(
                            onClick = {
                                if (uiState.menuState == UserInteractionState.STOPPED && !uiState.isLocked) {
                                    viewModel.removeAction(index)

                                    // If actions are few, animate scroll back to the start
                                    if (viewModel.actionCount() < 5) {
                                        coroutineScope.launch {
                                            scrollState.animateScrollTo(
                                                0,
                                                animationSpec = tween(
                                                    durationMillis = 750,
                                                    easing = FastOutSlowInEasing
                                                )
                                            )
                                        }
                                    }

                                    snapToClosestAction()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Bold Close",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders a normal action button, that is, an action that is not a special action.
 *
 * @param item The [UIAction] to be rendered.
 */
@Composable
private fun renderNormalActionButton(
    item: UIAction,
) {
    val appearance = GetActionAppearance(item.action.type)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {

        Text(
            text = String.format(
                Locale.getDefault(),
                "%.1f",
                item.durationRemaining
            ),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = actionFontsize


        )
        Icon(
            imageVector = appearance.icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size((70).dp)
        )
        Text(
            text = appearance.text,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = actionFontsize
        )
    }
}


/**
 * Renders a special action button, except for loop actions since they need special handling.
 *
 * @param item The [UIAction] to be rendered.
 */
@Composable
private fun renderSpecialActionButton(
    item: UIAction
) {

    if (item.action.type == RobotActionType.LOOP_START || item.action.type == RobotActionType.LOOP_END) {
        drawLoopActionButton(item)
    } else {
        val appearance = GetActionAppearance(item.action)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 5.dp, end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Icon(
                imageVector = appearance.icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size((70).dp)
                    .graphicsLayer(
                        // TODO: handle in a better way
                        rotationZ = if (item.action.data == "Finger gun") 90f else 0f
                    )
            )
            val actionName = if (item.action.data.isEmpty()) appearance.text else item.action.data
            Text(
                text = minimizeText(actionName, specialActionTextLimit),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = actionFontsize
            )
        }
    }
}

/**
 * Renders a loop button.
 *
 * @param item The [UIAction] to be rendered.
 */
@Composable
private fun drawLoopActionButton(
    item: UIAction
) {
    val appearance = GetActionAppearance(item.action.type)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Icon and loop parameter (e.g. number of loops)
            Icon(
                imageVector = appearance.icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size((80).dp)
            )

            if (item.action.type != RobotActionType.LOOP_END) {
                Text(
                    text = minimizeText(
                        item.iterations.toString(),
                        maxLength = 15
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = (actionFontsize.value + 5).sp

                )
            }
        }

        Text(
            text = appearance.text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontSize = actionFontsize,
                textAlign = TextAlign.Center
            )
        )
    }
}

/**
 * Shortens the given text to a specified maximum length, adding a suffix if truncation occurs.
 *
 * @param text The original text to be minimized.
 * @param maxLength The maximum allowed length for the text, including the suffix.
 * @param suffix The string to append to the truncated text (default is "...").
 * @return The minimized text, shortened if necessary, with the suffix added if truncation occurs.
 */
fun minimizeText(text: String, maxLength: Int, suffix: String = "..."): String {
    return if (text.length > maxLength) text.take(maxLength - suffix.length) + suffix else text
}

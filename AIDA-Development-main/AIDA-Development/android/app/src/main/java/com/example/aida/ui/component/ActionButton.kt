package com.example.aida.ui.component

import android.content.ClipData
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.constants.actionLibraryButtonHeight
import com.example.aida.ui.constants.actionLibraryButtonWidth
import com.example.aida.ui.viewmodel.SequenceBarState
import com.example.aida.ui.viewmodel.UserInteractionState

/**
 * A draggable button representing an action in the action library.
 *
 * @param actionType The [RobotActionType] that describes which type of action should be drawn.
 *     The appearance of the action can be derived from this type.
 * @param onClick A lambda that descibes what should happen when clicking the button.
 * @param uiState The [SequenceBarState] that contains the UI state of the sequence bar.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionButton(
    actionType: RobotActionType,
    onClick: (() -> Unit),
    uiState: SequenceBarState,
) {
    // Get the color and icon for the current action
    val appearance = GetActionAppearance(actionType)

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                // Only allow adding if we aren't playing
                if (uiState.menuState == UserInteractionState.STOPPED) {
                    detectTapGestures(
                        onPress = {
                            tryAwaitRelease()
                        },
                        onTap = {
                            onClick.invoke()
                        },
                        onDoubleTap = {
                            onClick.invoke()
                        }
                    )
                }
            }
            .dragAndDropSource {
                // Only allow adding if we aren't playing
                    detectDragGestures(
                        onDrag = { _, _ ->
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newPlainText(
                                        appearance.text,
                                        actionType.name
                                    ),
                                    flags = View.DRAG_FLAG_GLOBAL or View.DRAG_FLAG_OPAQUE, // Combine flags with bitwise OR
                                )
                            )
                        }
                    )
            }
            .size(
                actionLibraryButtonWidth,
                actionLibraryButtonHeight
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = GetActionColor(actionType))
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color.Gray,
                ambientColor = Color.Black,
            ),
        contentAlignment = Alignment.Center
    ) {
        // Button content: Icon and Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = appearance.icon,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appearance.text,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

    }
}
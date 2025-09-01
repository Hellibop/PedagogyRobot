package com.example.aida.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.aida.ui.viewmodel.SequenceViewModel
import com.example.aida.ui.viewmodel.UserInteractionState

/**
 * A button that clears all actions from the sequence bar.
 *
 * @param viewModel [SequenceViewModel] that contains the UI state of the sequence bar.
 * @param modifier [Modifier] for layout or styling.
 * @param snapToClosestAction A lambda that snaps to the closest action.
 */
@Composable
fun ClearSequenceButton(
    viewModel: SequenceViewModel,
    modifier: Modifier = Modifier,
    snapToClosestAction: () -> Unit
){
    Button(
        onClick = {
            if (!viewModel.getLockedState() && (viewModel.getState() == UserInteractionState.STOPPED)) {
                viewModel.clearActions()

                // Align to the padding block
                snapToClosestAction()
            }
        },
        modifier = modifier
    ) {
        Text(
            text = "Clear Sequence",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}
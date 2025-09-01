package com.example.aida.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.aida.ui.constants.lockButtonColor
import com.example.aida.ui.viewmodel.SequenceBarState
import com.example.aida.ui.viewmodel.SequenceViewModel

/**
 * A button that opens the QR-scanner that imports code from the webapp.
 * @param modifier Optional [Modifier] for layout or styling.
 * @param viewModel The [SequenceViewModel] that contains the UI state of the sequence bar.
 * @param uiState The [SequenceBarState] that contains the UI state of the sequence bar.
 */
@Composable
fun LockButton(
    viewModel: SequenceViewModel,
    modifier: Modifier = Modifier,
    uiState: SequenceBarState
) {
    IconButton(
        onClick = {
            // Flip the locked state
            viewModel.setLockState(!viewModel.getLockedState())
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (uiState.isLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
            contentDescription = "Stop",
            tint = lockButtonColor,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
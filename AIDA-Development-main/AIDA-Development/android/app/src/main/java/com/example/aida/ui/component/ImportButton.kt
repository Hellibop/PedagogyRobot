// ImportButton.kt
package com.example.aida.ui.component

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.aida.ui.popups.PopupQrScanner
import com.example.aida.ui.viewmodel.SequenceViewModel
import com.example.aida.ui.viewmodel.UserInteractionState

/**
 * A button that opens the QR-scanner that imports code from the webapp.
 * @param modifier Optional [Modifier] for layout or styling.
 * @param viewModel [SequenceViewModel] that contains the UI state of the sequence bar.
 */
@Composable
fun ImportButton(
    modifier: Modifier = Modifier,
    viewModel: SequenceViewModel
) {
    var showScanner by remember { mutableStateOf(false) }

    Log.d("ImportButton", "ImportButton rendering with showScanner=$showScanner")

    Button(
        onClick = {
            Log.d("ImportButton", "Import button clicked")
            showScanner = true
        },
        modifier = modifier,
        enabled = (viewModel.getState() == UserInteractionState.STOPPED)
    ) {
        Text(
            text = "Import",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }

    // When showScanner is true, display the QR scanner dialog
    if (showScanner) {
        Log.d("ImportButton", "Showing QR scanner dialog")
        PopupQrScanner(
            onDismiss = { success ->
                showScanner = false

                if (success) {
                    viewModel.setLockState(true)
                }
            }
        )
    }
}

package com.example.aida.ui.popups

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aida.ui.component.QrCodeScannerView
import com.example.aida.ui.viewmodel.QrScannerViewModel

/**
 * Popup that displays the QR code scanner.
 *
 * @param onDismiss Callback that is called upon closing popup. Gets called with a boolean
 * value that describes whether scanning succeeded or not.
 */
@Composable
fun PopupQrScanner(
    onDismiss: (Boolean) -> Unit,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Check if we have camera permission
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("QRScanner", "Camera permission granted: $granted")
        hasPermission = granted
    }

    // Request permission if needed
    LaunchedEffect(Unit) {
        Log.d("QRScanner", "Checking camera permission: $hasPermission")
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show appropriate screen based on permission
    if (hasPermission) {
        Log.d("QRScanner", "Showing QR scanner with permission")
        QrScannerScreen(
            onCodeScanned = { code ->
                val result = viewModel.onQRCodeScanned(code)
                onDismiss(result)
            },
            onCancel = { onDismiss(false) }
        )
    } else {
        // Permission request screen
        Dialog(
            onDismissRequest = { onDismiss(false) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Camera permission is required to scan QR codes.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    Log.d("QRScanner", "Requesting camera permission")
                    launcher.launch(Manifest.permission.CAMERA)
                    hasPermission = true
                }) {
                    Text("Grant Permission")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        onDismiss(false)
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * Screen for the QR scanner for importing sequences.
 *
 * @param onCodeScanned Callback that is called upon finishing scanning. Gets called
 * with the string that was parsed from the QR code.
 * @param onCancel Callback that is called upon exiting the screen.
 */
@Composable
private fun QrScannerScreen(
    onCodeScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Black)
                .padding(8.dp)
        ) {
            QrCodeScannerView(
                onQrCodeScanned = { result ->
                    Log.d("QRScanner", "QR code scanned in dialog: $result")
                    onCodeScanned(result)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Add a cancel button
            Button(
                onClick = {
                    Log.d("QRScanner", "Cancel button clicked")
                    onCancel()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}
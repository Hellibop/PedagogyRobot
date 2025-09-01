package com.example.aida.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aida.R
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.ui.theme.TopBarColor
import com.example.aida.ui.viewmodel.TopBarViewModel

/**
 * Function for the top bar, contains both UI and logic, however some
 * widgets need to be decided on what they should do.
 *
 * @param onMenuClicked records if the menu button has been pressed
 * @param barHeight used to determine the size of the top bar
 * @author Elias
 */
@Composable
fun TopBar(
    onMenuClicked: () -> Unit,
    barHeight: Dp,
    onToggleClicked: (Boolean) -> Unit,
    toggleState: Boolean,
    viewModel: TopBarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val barPadding = 15.dp
    val topPadding = 4.dp

    Row(
        modifier = Modifier
            .height(barHeight)
            .fillMaxWidth()
            .background(TopBarColor.copy(alpha = 0.6f))
            .padding(start = barPadding, end = barPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set the menu icon
        Row(
            modifier = Modifier
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_icon),
                contentDescription = "configuration",
                Modifier
                    .clickable(onClick = onMenuClicked)
                    .scale(1.2f)
            )
        }

        // Text fields on either side of the switch
        Row(

            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left button "Manual Control"
                Text(
                    text = "Manual Control",
                    fontSize = 16.sp,
                    color = if (toggleState) Color.Black else Color(0xFF0A6EBD),
                    style = if (!toggleState) {
                        TextStyle(
                            shadow = Shadow(
                                color = Color(0x33000000),
                                offset = Offset(4f, 4f),
                                blurRadius = 2f
                            )
                        )
                    } else {
                        TextStyle()
                    },
                    modifier = Modifier
                        .clickable { onToggleClicked(false) }
                        .padding(end = 8.dp)
                )

                // Toggle Switch
                Switch(
                    checked = toggleState,
                    onCheckedChange = { isChecked ->
                        onToggleClicked(isChecked)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0A6EBD),
                        uncheckedThumbColor = Color(0xFF0A6EBD),
                        checkedTrackColor = Color.White,
                        uncheckedTrackColor = Color.White
                    )
                )

                // Left button "Input Sequence"
                Text(
                    text = "Input Sequence",
                    fontSize = 16.sp,
                    color = if (!toggleState) Color.Black else Color(0xFF0A6EBD),
                    style = if (toggleState) {
                        TextStyle(
                            shadow = Shadow(
                                color = Color(0x33000000),
                                offset = Offset(4f, 4f),
                                blurRadius = 2f
                            )
                        )
                    } else {
                        TextStyle()
                    },
                    modifier = Modifier
                        .clickable { onToggleClicked(true) }
                        .padding(start = 8.dp)
                )
            }
        }

            // if not in sequence tab, show icons
        if(!toggleState)
        {
            // Set camera and volume on/off buttons
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Spacer(Modifier.weight(5f))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .clickable(
                            enabled = (uiState.connectionStates[ConnectionId.VIDEO]
                                is ConnectionState.Connected),
                            onClick = { viewModel.onCameraButtonClick() }
                        )
                        .padding(top = topPadding)
                        .alpha(
                            if (uiState.connectionStates[ConnectionId.VIDEO]
                                        is ConnectionState.Connected) 1.0f
                            else 0.3f
                        ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.videocam),
                        contentDescription = "camera",
                        Modifier
                            .scale(1f)
                    )
                    Text(
                        text = if (uiState.isCameraEnabled && uiState.connectionStates[ConnectionId.VIDEO]
                                    is ConnectionState.Connected) "on" else "off",
                        Modifier
                            .offset(y = (-2).dp)
                    )
                }
                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            enabled = (uiState.connectionStates[ConnectionId.STT]
                                    is ConnectionState.Connected),
                            onClick = {viewModel.onMicButtonClick()}
                        )
                        .padding(top = topPadding)
                        .alpha(
                            if (uiState.connectionStates[ConnectionId.STT]
                                        is ConnectionState.Connected) 1.0f
                            else 0.3f
                        ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mic_button_400),
                        contentDescription = "camera",
                        Modifier
                            .scale(1f)
                    )
                    Text(
                        text = if (uiState.isMicEnabled && uiState.connectionStates[ConnectionId.STT]
                                    is ConnectionState.Connected) "on" else "off",
                        Modifier
                            .offset(y = (-2).dp)
                    )
                }
                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .clickable(
                            enabled = (uiState.connectionStates[ConnectionId.GESTURE]
                                    is ConnectionState.Connected),
                            onClick = { viewModel.onGestureButtonClick() }
                        )
                        .padding(top = topPadding)
                        .alpha(
                            if (uiState.connectionStates[ConnectionId.GESTURE]
                                        is ConnectionState.Connected) 1.0f
                            else 0.3f
                        ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hand_gesture),
                        contentDescription = "gesture",
                        Modifier
                            .scale(1f)
                    )
                    Text(
                        text = if (uiState.isGestureEnabled && uiState.connectionStates[ConnectionId.GESTURE]
                                    is ConnectionState.Connected) "on" else "off",
                        Modifier
                            .offset(y = (-2).dp)
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
        else {
            Row(
                modifier = Modifier.weight(1f),
            ) {}
        }
    }
}

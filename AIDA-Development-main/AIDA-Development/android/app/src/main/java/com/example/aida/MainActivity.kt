package com.example.aida

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aida.ui.component.TopBar
import com.example.aida.ui.navigation.CameraRoute
import com.example.aida.ui.navigation.ConfigurationRoute
import com.example.aida.ui.navigation.NavGraph
import com.example.aida.ui.navigation.SequenceTabRoute
import com.example.aida.ui.navigation.UserGuideRoute
import com.example.aida.ui.theme.AIDATheme
import com.example.aida.ui.viewmodel.SequenceViewModel
import com.example.aida.ui.viewmodel.TopBarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The main activity of the application, serving as the entry point and host for the UI.
 * It sets up the main screen content using Jetpack Compose and manages navigation.
 *
 * @property sequenceViewModel An instance of [SequenceViewModel] used to manage
 *      and persist robot action sequences.
 * @suppress
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sequenceViewModel : SequenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AIDATheme {
                MainScreen()
            }
        }
    }

    /**
     * Called when the activity is no longer visible to the user.
     * This is where sequence actions are saved to ensure data persistence.
     */
    override fun onStop() {
        super.onStop()

        // Save current actions in the sequence view model when the app is stopped
        sequenceViewModel.saveActions()
    }

    /**
     * Composable function that sets up the main application UI, including the
     * navigation drawer, top bar, and the navigation graph for different screens.
     *
     * @param navController The [NavHostController] for managing app navigation.
     */
    @Composable
    private fun AppContent(navController: NavHostController) {
        val topBarViewModel: TopBarViewModel = hiltViewModel()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // State to manage the toggle button in the TopBar, synced with the current route.
        var toggleState by remember { mutableStateOf(false) }

        // The toggle is active if the current route is the SequenceTabRoute.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toggleState = destination.route == SequenceTabRoute.routeDefinition
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false,
            drawerContent = {
                ModalDrawerSheet {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Menu", modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Drawer")
                        }
                    }
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text(text = "Home") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate(CameraRoute.routeDefinition)
                                drawerState.close()
                            }
                        }
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        },
                        label = { Text(text = "Connection Settings") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate(ConfigurationRoute.routeDefinition)
                                drawerState.close()
                            }
                        }
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "User Guide"
                            )
                        },
                        label = { Text(text = "User Guide") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate(UserGuideRoute.routeDefinition)
                                drawerState.close()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Shutdown application"
                            )
                        },
                        label = { Text(text = "Application shutdown", color = Color.Red) },
                        selected = false,
                        onClick = { System.exit(0) }
                    )
                }
            },
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {

                // Determine dynamic TopBar height based on screen height.
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val barHeight = if (screenHeight / 8 < 50.dp) screenHeight / 6 else 50.dp

                Column {
                    TopBar(
                        onMenuClicked = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        barHeight = barHeight,
                        onToggleClicked = { toggled ->

                            if (toggled)
                                navController.navigate(SequenceTabRoute.routeDefinition)
                            else
                                navController.navigate(CameraRoute.routeDefinition)

                            //topBarViewModel.onToggleClicked(toggled)

                        },
                        toggleState = toggleState,
                        viewModel = topBarViewModel
                    )

                    NavGraph(navController = navController)
                }
            }
        }
    }

    /**
     * Composable function that sets up the [NavController] and renders the [AppContent].
     * This is the root composable for the main screen.
     */
    @Composable
    private fun MainScreen() {
        var navController = rememberNavController()
        AppContent(navController)
    }

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview() {}
}


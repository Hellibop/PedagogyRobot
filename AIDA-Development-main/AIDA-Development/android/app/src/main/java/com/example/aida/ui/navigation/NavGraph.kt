package com.example.aida.ui.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aida.MainActivity
import com.example.aida.ui.page.CameraPage
import com.example.aida.ui.page.ConfigurationPage
import com.example.aida.ui.page.SequenceTabPage
import com.example.aida.ui.page.UserGuidePage
import com.example.aida.ui.viewmodel.SplashScreen

/**
 * Navigation handler for navigating between different pages defined in [com.example.aida.ui.page].
 *
 * @param navController The [NavHostController] created in [MainActivity].
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute.routeDefinition
    ){
        addSplashScreen(navController, this)
        addGuidePage(this)
        addCameraPage(this)
        addSequenceTabPage(this)
        addConfigurationPage(this)
    }
}

private fun addSplashScreen(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = SplashRoute.routeDefinition) {
        SplashScreen(navigateToMain = {
            navController.navigate(CameraRoute.routeDefinition) {
                popUpTo(SplashRoute.routeDefinition) { inclusive = true }
            }
        })
    }
}

private fun addGuidePage(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = UserGuideRoute.routeDefinition) {
        val mainActivity = LocalActivity.current as MainActivity

        UserGuidePage(
            activity = mainActivity
        )
    }
}

private fun addCameraPage(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = CameraRoute.routeDefinition) {
        CameraPage()
    }
}

private fun addSequenceTabPage(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = SequenceTabRoute.routeDefinition) {
        SequenceTabPage()     }
}

private fun addConfigurationPage(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = ConfigurationRoute.routeDefinition) {
        ConfigurationPage()
    }
}

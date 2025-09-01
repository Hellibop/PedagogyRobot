package com.example.aida.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Base class for all routes. Needed to convert class name to string in order to turn into a route.
 */
@Serializable
sealed class TopLevelRoute {
    /**
     * Converts a route class name to a string. Used when switching to a route.
     *
     * @return A [String] containing the name of the class.
     *
     */
    val routeDefinition: String
        get() = this::class.qualifiedName!!
}

/**
 * Route to the splash screen launched when opening the app.
 */
@Serializable
object SplashRoute : TopLevelRoute()

/**
 * Route to the user guide page.
 */
@Serializable
object UserGuideRoute : TopLevelRoute()

/**
 * Route to the settings page.
 */
@Serializable
object ConfigurationRoute : TopLevelRoute()

/**
 * Route to the camera page.
 */
@Serializable
object CameraRoute : TopLevelRoute()

/**
 * Route to the sequence tab page.
 */
@Serializable
object SequenceTabRoute: TopLevelRoute()
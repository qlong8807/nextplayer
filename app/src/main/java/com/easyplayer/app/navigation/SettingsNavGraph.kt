package com.easyplayer.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.easyplayer.app.settings.Setting
import com.easyplayer.app.settings.navigation.aboutPreferencesScreen
import com.easyplayer.app.settings.navigation.appearancePreferencesScreen
import com.easyplayer.app.settings.navigation.audioPreferencesScreen
import com.easyplayer.app.settings.navigation.decoderPreferencesScreen
import com.easyplayer.app.settings.navigation.folderPreferencesScreen
import com.easyplayer.app.settings.navigation.generalPreferencesScreen
import com.easyplayer.app.settings.navigation.librariesScreen
import com.easyplayer.app.settings.navigation.mediaLibraryPreferencesScreen
import com.easyplayer.app.settings.navigation.navigateToAboutPreferences
import com.easyplayer.app.settings.navigation.navigateToAppearancePreferences
import com.easyplayer.app.settings.navigation.gesturePreferencesScreen
import com.easyplayer.app.settings.navigation.navigateToAudioPreferences
import com.easyplayer.app.settings.navigation.navigateToDecoderPreferences
import com.easyplayer.app.settings.navigation.navigateToGesturePreferences
import com.easyplayer.app.settings.navigation.navigateToFolderPreferencesScreen
import com.easyplayer.app.settings.navigation.navigateToGeneralPreferences
import com.easyplayer.app.settings.navigation.navigateToLibraries
import com.easyplayer.app.settings.navigation.navigateToMediaLibraryPreferencesScreen
import com.easyplayer.app.settings.navigation.navigateToPlayerPreferences
import com.easyplayer.app.settings.navigation.navigateToSubtitlePreferences
import com.easyplayer.app.settings.navigation.navigateToThumbnailPreferencesScreen
import com.easyplayer.app.settings.navigation.playerPreferencesScreen
import com.easyplayer.app.settings.navigation.settingsNavigationRoute
import com.easyplayer.app.settings.navigation.settingsScreen
import com.easyplayer.app.settings.navigation.subtitlePreferencesScreen
import com.easyplayer.app.settings.navigation.thumbnailPreferencesScreen

const val SETTINGS_ROUTE = "settings_nav_route"

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = settingsNavigationRoute,
        route = SETTINGS_ROUTE,
    ) {
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            onItemClick = { setting ->
                when (setting) {
                    Setting.APPEARANCE -> navController.navigateToAppearancePreferences()
                    Setting.MEDIA_LIBRARY -> navController.navigateToMediaLibraryPreferencesScreen()
                    Setting.PLAYER -> navController.navigateToPlayerPreferences()
                    Setting.GESTURES -> navController.navigateToGesturePreferences()
                    Setting.DECODER -> navController.navigateToDecoderPreferences()
                    Setting.AUDIO -> navController.navigateToAudioPreferences()
                    Setting.SUBTITLE -> navController.navigateToSubtitlePreferences()
                    Setting.GENERAL -> navController.navigateToGeneralPreferences()
                    Setting.ABOUT -> navController.navigateToAboutPreferences()
                }
            },
        )
        appearancePreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        mediaLibraryPreferencesScreen(
            onNavigateUp = navController::navigateUp,
            onFolderSettingClick = navController::navigateToFolderPreferencesScreen,
            onThumbnailSettingClick = navController::navigateToThumbnailPreferencesScreen,
        )
        thumbnailPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        folderPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        playerPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        gesturePreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        decoderPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        audioPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        subtitlePreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        generalPreferencesScreen(
            onNavigateUp = navController::navigateUp,
        )
        aboutPreferencesScreen(
            onLibrariesClick = navController::navigateToLibraries,
            onNavigateUp = navController::navigateUp,
        )
        librariesScreen(
            onNavigateUp = navController::navigateUp,
        )
    }
}

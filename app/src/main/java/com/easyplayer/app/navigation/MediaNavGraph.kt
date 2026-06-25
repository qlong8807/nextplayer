package com.easyplayer.app.navigation

import android.content.Context
import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.easyplayer.app.feature.player.PlayerActivity
import com.easyplayer.app.feature.player.utils.PlayerApi
import com.easyplayer.app.feature.videopicker.navigation.MediaPickerRoute
import com.easyplayer.app.feature.videopicker.navigation.mediaPickerScreen
import com.easyplayer.app.feature.videopicker.navigation.navigateToMediaPickerScreen
import com.easyplayer.app.feature.videopicker.navigation.navigateToSearch
import com.easyplayer.app.feature.videopicker.navigation.searchScreen
import com.easyplayer.app.settings.navigation.navigateToSettings
import kotlinx.serialization.Serializable

@Serializable
data object MediaRootRoute

fun NavGraphBuilder.mediaNavGraph(
    context: Context,
    navController: NavHostController,
) {
    navigation<MediaRootRoute>(startDestination = MediaPickerRoute()) {
        mediaPickerScreen(
            onNavigateUp = navController::navigateUp,
            onPlayVideo = { uri ->
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                }
                context.startActivity(intent)
            },
            onPlayVideos = { uris ->
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uris.first()
                    putParcelableArrayListExtra(PlayerApi.API_PLAYLIST, ArrayList(uris))
                }
                context.startActivity(intent)
            },
            onFolderClick = navController::navigateToMediaPickerScreen,
            onSettingsClick = navController::navigateToSettings,
            onSearchClick = navController::navigateToSearch,
        )

        searchScreen(
            onNavigateUp = navController::navigateUp,
            onPlayVideo = { uri ->
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                }
                context.startActivity(intent)
            },
            onFolderClick = navController::navigateToMediaPickerScreen,
        )
    }
}

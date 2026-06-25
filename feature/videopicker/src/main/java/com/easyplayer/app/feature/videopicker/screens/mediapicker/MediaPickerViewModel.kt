package com.easyplayer.app.feature.videopicker.screens.mediapicker

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.easyplayer.app.core.common.extensions.prettyName
import com.easyplayer.app.core.data.repository.MediaRepository
import com.easyplayer.app.core.data.repository.PreferencesRepository
import com.easyplayer.app.core.domain.GetRecentlyPlayedVideoUseCase
import com.easyplayer.app.core.domain.GetSortedMediaUseCase
import com.easyplayer.app.core.domain.GetSortedVideosUseCase
import com.easyplayer.app.core.domain.MediaHolder
import com.easyplayer.app.core.media.services.MediaOperationsService
import com.easyplayer.app.core.media.sync.MediaSynchronizer
import com.easyplayer.app.core.model.ApplicationPreferences
import com.easyplayer.app.core.model.Folder
import com.easyplayer.app.core.model.MediaViewMode
import com.easyplayer.app.core.model.Video
import com.easyplayer.app.core.model.findClosestFolder
import com.easyplayer.app.core.ui.base.DataState
import com.easyplayer.app.feature.videopicker.navigation.FolderArgs
import com.easyplayer.app.feature.videopicker.state.SelectionItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaPickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSortedMediaUseCase: GetSortedMediaUseCase,
    private val getRecentlyPlayedVideoUseCase: GetRecentlyPlayedVideoUseCase,
    private val getSortedVideosUseCase: GetSortedVideosUseCase,
    private val mediaOperationsService: MediaOperationsService,
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository,
    private val mediaSynchronizer: MediaSynchronizer,
) : ViewModel() {

    private val folderArgs = FolderArgs(savedStateHandle)
    val folderPath = folderArgs.folderId

    private val uiStateInternal = MutableStateFlow(
        MediaPickerUiState(
            folderName = folderPath?.let { File(folderPath).prettyName },
            preferences = preferencesRepository.applicationPreferences.value,
        ),
    )
    val uiState = uiStateInternal.asStateFlow()

    private val eventsInternal = Channel<MediaPickerEvent>()
    val events = eventsInternal.receiveAsFlow()

    private var mediaCollectJob: Job? = null

    init {
        collectMedia()
        collectPreferences()
    }

    fun onAction(action: MediaPickerAction) {
        when (action) {
            is MediaPickerAction.Refresh -> refresh()
            is MediaPickerAction.RenameVideo -> renameVideo(action.uri, action.to)
            is MediaPickerAction.UpdateMenu -> updateMenu(action.preferences)
            is MediaPickerAction.OnPermissionAccepted -> collectMedia()
            is MediaPickerAction.PlaySelectedItems -> playSelectedItems(action.selectionItems)
            is MediaPickerAction.DeleteSelectedItems -> deleteSelectedItems(action.selectionItems)
            is MediaPickerAction.ShareSelectedItems -> shareSelectedItems(action.selectionItems)
            is MediaPickerAction.TogglePinFolders -> togglePinFolders(action.selectionItems)
            is MediaPickerAction.ShowMediaInfo -> showMediaInfo(action.video)
            MediaPickerAction.DismissMediaInfo -> uiStateInternal.update { it.copy(mediaInfo = null) }
        }
    }

    private fun collectMedia() {
        mediaCollectJob?.cancel()
        uiStateInternal.update { currentState ->
            currentState.copy(mediaDataState = DataState.Loading)
        }
        mediaCollectJob = viewModelScope.launch {
            combine(
                getSortedMediaUseCase.invoke(folderPath),
                getRecentlyPlayedVideoUseCase.invoke(folderPath),
            ) { media, recentlyPlayed ->
                media to recentlyPlayed
            }.collect { (media, recentlyPlayed) ->
                uiStateInternal.update { currentState ->
                    currentState.copy(
                        mediaDataState = DataState.Success(media),
                        recentlyPlayedVideo = recentlyPlayed,
                        recentlyPlayedFolder = recentlyPlayed?.let { media?.folders?.findClosestFolder(it.path) }
                    )
                }
            }
        }
    }

    private fun collectPreferences() {
        viewModelScope.launch {
            preferencesRepository.applicationPreferences.collect {
                uiStateInternal.update { currentState ->
                    currentState.copy(preferences = it)
                }
            }
        }
    }

    private fun playSelectedItems(selectedItems: Set<SelectionItem>) {
        viewModelScope.launch {
            val videoUris = selectedItems.toVideoUris()
            eventsInternal.send(MediaPickerEvent.PlayVideos(videoUris))
        }
    }

    private fun deleteSelectedItems(selectedItems: Set<SelectionItem>) {
        viewModelScope.launch {
            val videoUris = selectedItems.toVideoUris()
            mediaOperationsService.deleteMedia(videoUris)
        }
    }

    private fun shareSelectedItems(selectedItems: Set<SelectionItem>) {
        viewModelScope.launch {
            val videoUris = selectedItems.toVideoUris()
            mediaOperationsService.shareMedia(videoUris)
        }
    }

    private fun showMediaInfo(video: Video) {
        viewModelScope.launch {
            val mediaInfo = mediaRepository.getMediaInfo(video.uriString)
            if (mediaInfo != null) {
                uiStateInternal.update { it.copy(mediaInfo = mediaInfo) }
            }
        }
    }

    private fun renameVideo(uri: Uri, to: String) {
        viewModelScope.launch {
            mediaOperationsService.renameMedia(uri, to)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            uiStateInternal.update { it.copy(refreshing = true) }
            mediaSynchronizer.refresh()
            uiStateInternal.update { it.copy(refreshing = false) }
        }
    }

    private fun togglePinFolders(selectedItems: Set<SelectionItem>) {
        viewModelScope.launch {
            val folderPaths = selectedItems
                .filterIsInstance<SelectionItem.Folder>()
                .map { it.path }
            if (folderPaths.isEmpty()) return@launch

            val now = System.currentTimeMillis()
            preferencesRepository.updateApplicationPreferences { prefs ->
                val currentPinned = prefs.pinnedFolders
                // 已置顶的取消置顶，未置顶的添加置顶
                val toUnpin = folderPaths.filter { it in currentPinned }
                val toPin = folderPaths.filter { it !in currentPinned }

                val updated = currentPinned.toMutableMap()
                toUnpin.forEach { updated.remove(it) }
                toPin.forEach { updated[it] = now }
                prefs.copy(pinnedFolders = updated)
            }
        }
    }

    private fun updateMenu(preferences: ApplicationPreferences) {
        viewModelScope.launch {
            preferencesRepository.updateApplicationPreferences { preferences }
        }
    }

    private suspend fun Set<SelectionItem>.toVideoUris(): List<Uri> {
        val preferences = uiStateInternal.value.preferences
        return flatMap { selectionItem ->
            when (selectionItem) {
                is SelectionItem.Video -> listOf(selectionItem.uriString.toUri())
                is SelectionItem.Folder -> {
                    val videos = getSortedVideosUseCase(selectionItem.path).first()
                    // In FOLDERS mode, only include direct children
                    val filteredVideos = if (preferences.mediaViewMode == MediaViewMode.FOLDERS) {
                        videos.filter { it.parentPath == selectionItem.path }
                    } else {
                        videos
                    }
                    filteredVideos.map { it.uriString.toUri() }
                }
            }
        }
    }
}

@Stable
data class MediaPickerUiState(
    val folderName: String?,
    val refreshing: Boolean = false,
    val recentlyPlayedVideo: Video? = null,
    val recentlyPlayedFolder: Folder? = null,
    val mediaDataState: DataState<MediaHolder?> = DataState.Loading,
    val preferences: ApplicationPreferences = ApplicationPreferences(),
    val mediaInfo: com.easyplayer.app.core.model.MediaInfo? = null,
)

sealed interface MediaPickerAction {
    data object Refresh : MediaPickerAction
    data class RenameVideo(val uri: Uri, val to: String) : MediaPickerAction
    data class UpdateMenu(val preferences: ApplicationPreferences) : MediaPickerAction
    data object OnPermissionAccepted : MediaPickerAction
    data class PlaySelectedItems(val selectionItems: Set<SelectionItem>) : MediaPickerAction
    data class DeleteSelectedItems(val selectionItems: Set<SelectionItem>) : MediaPickerAction
    data class ShareSelectedItems(val selectionItems: Set<SelectionItem>) : MediaPickerAction
    data class TogglePinFolders(val selectionItems: Set<SelectionItem>) : MediaPickerAction
    data class ShowMediaInfo(val video: Video): MediaPickerAction
    data object DismissMediaInfo : MediaPickerAction
}

sealed interface MediaPickerEvent {
    data class PlayVideos(val uris: List<Uri>) : MediaPickerEvent
}

package com.easyplayer.app.core.domain

import com.easyplayer.app.core.common.Dispatcher
import com.easyplayer.app.core.common.NextDispatchers
import com.easyplayer.app.core.data.repository.MediaRepository
import com.easyplayer.app.core.data.repository.PreferencesRepository
import com.easyplayer.app.core.model.Sort
import com.easyplayer.app.core.model.Video
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetSortedVideosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository,
    @Dispatcher(NextDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(folderPath: String? = null): Flow<List<Video>> {
        return combine(
            mediaRepository.observeVideos(folderPath),
            preferencesRepository.applicationPreferences,
        ) { videoItems, preferences ->

            val nonExcludedVideos = videoItems.filterNot {
                it.parentPath in preferences.excludeFolders
            }

            val sort = Sort(by = preferences.sortBy, order = preferences.sortOrder)
            nonExcludedVideos.sortedWith(sort.videoComparator())
        }.flowOn(defaultDispatcher)
    }
}

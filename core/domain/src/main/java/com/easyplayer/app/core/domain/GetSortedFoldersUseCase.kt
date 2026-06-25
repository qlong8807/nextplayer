package com.easyplayer.app.core.domain

import com.easyplayer.app.core.common.Dispatcher
import com.easyplayer.app.core.common.NextDispatchers
import com.easyplayer.app.core.data.repository.MediaRepository
import com.easyplayer.app.core.data.repository.PreferencesRepository
import com.easyplayer.app.core.model.Folder
import com.easyplayer.app.core.model.Sort
import com.easyplayer.app.core.model.sortedWithPinned
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetSortedFoldersUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository,
    @Dispatcher(NextDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(folderPath: String? = null): Flow<List<Folder>> {
        return combine(
            mediaRepository.observeFolders(folderPath),
            preferencesRepository.applicationPreferences,
        ) { folders, preferences ->

            val nonExcludedDirectories = folders.filter {
                it.path !in preferences.excludeFolders
            }

            val sort = Sort(by = preferences.sortBy, order = preferences.sortOrder)
            nonExcludedDirectories.sortedWith(sort.folderComparator())
                .sortedWithPinned(preferences.pinnedFolders)
        }.flowOn(defaultDispatcher)
    }
}

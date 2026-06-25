package com.easyplayer.app.core.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.easyplayer.app.core.data.repository.LocalMediaRepository
import com.easyplayer.app.core.data.repository.LocalPreferencesRepository
import com.easyplayer.app.core.data.repository.LocalSearchHistoryRepository
import com.easyplayer.app.core.data.repository.MediaRepository
import com.easyplayer.app.core.data.repository.PreferencesRepository
import com.easyplayer.app.core.data.repository.SearchHistoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsMediaRepository(
        videoRepository: LocalMediaRepository,
    ): MediaRepository

    @Binds
    @Singleton
    fun bindsPreferencesRepository(
        preferencesRepository: LocalPreferencesRepository,
    ): PreferencesRepository

    @Binds
    @Singleton
    fun bindsSearchHistoryRepository(
        searchHistoryRepository: LocalSearchHistoryRepository,
    ): SearchHistoryRepository
}

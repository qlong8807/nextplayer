package com.easyplayer.app.feature.videopicker.screens

import com.easyplayer.app.core.model.Folder

sealed interface MediaState {
    data object Loading : MediaState
    data class Success(val data: Folder?) : MediaState
}

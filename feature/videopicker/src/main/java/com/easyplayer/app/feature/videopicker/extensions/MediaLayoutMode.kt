package com.easyplayer.app.feature.videopicker.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.easyplayer.app.core.model.MediaLayoutMode
import com.easyplayer.app.core.ui.R

@Composable
fun MediaLayoutMode.name(): String {
    return when (this) {
        MediaLayoutMode.LIST -> stringResource(id = R.string.list)
        MediaLayoutMode.GRID -> stringResource(id = R.string.grid)
    }
}

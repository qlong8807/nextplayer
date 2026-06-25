package com.easyplayer.app.settings.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.easyplayer.app.core.model.FastSeek
import com.easyplayer.app.core.ui.R

@Composable
fun FastSeek.name(): String {
    val stringRes = when (this) {
        FastSeek.AUTO -> R.string.auto
        FastSeek.ENABLE -> R.string.enable
        FastSeek.DISABLE -> R.string.disable
    }

    return stringResource(id = stringRes)
}

package com.easyplayer.app.core.data.mappers

import com.easyplayer.app.core.media.services.MediaFolder
import com.easyplayer.app.core.model.Folder

internal fun MediaFolder.toFolder() = Folder(
    name = name,
    path = path,
    dateModified = dateModified,
    totalSize = totalSize,
    totalDuration = totalDuration,
    videosCount = videosCount,
    foldersCount = foldersCount,
)

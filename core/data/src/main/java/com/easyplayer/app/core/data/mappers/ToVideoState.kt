package com.easyplayer.app.core.data.mappers

import com.easyplayer.app.core.data.models.VideoState
import com.easyplayer.app.core.database.converter.UriListConverter
import com.easyplayer.app.core.database.entities.MediumStateEntity

fun MediumStateEntity.toVideoState(): VideoState {
    return VideoState(
        path = uriString,
        position = playbackPosition.takeIf { it != 0L },
        audioTrackIndex = audioTrackIndex,
        subtitleTrackIndex = subtitleTrackIndex,
        playbackSpeed = playbackSpeed,
        externalSubs = UriListConverter.fromStringToList(externalSubs),
        videoScale = videoScale,
        subtitleDelayMilliseconds = subtitleDelayMilliseconds,
        subtitleSpeed = subtitleSpeed,
    )
}

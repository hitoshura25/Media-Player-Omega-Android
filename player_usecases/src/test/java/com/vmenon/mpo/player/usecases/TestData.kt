package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlaybackState

object TestData {
    val playbackMedia = PlaybackMedia(
        mediaId = "mediaId",
        durationInMillis = 120000L
    )
    val playbackMediaRequest = PlaybackMediaRequest(
        media = playbackMedia,
        mediaFile = "file"
    )
    val playbackState = PlaybackState(
        media = playbackMedia,
        positionInMillis = 0L,
        state = PlaybackState.State.NONE,
        playbackSpeed = 1F
    )
}
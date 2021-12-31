package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest

object TestData {
    val playbackMediaRequest = PlaybackMediaRequest(
        media = PlaybackMedia(
            mediaId = "mediaId",
            durationInMillis = 10L
        ),
        mediaFile = "file"
    )
}
package com.vmenon.mpo.model

import androidx.room.Embedded

data class DownloadWithShowAndEpisodeDetailsModel(
    @Embedded val download: DownloadModel,
    @Embedded val episode: EpisodeDetailsModel,
    @Embedded val show: ShowDetailsModel
)
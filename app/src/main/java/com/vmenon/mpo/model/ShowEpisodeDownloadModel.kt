package com.vmenon.mpo.model

import androidx.room.Embedded

data class ShowEpisodeDownloadModel(
    @Embedded val download: DownloadModel,
    @Embedded val episode: EpisodeModel,
    @Embedded val show: ShowModel
)
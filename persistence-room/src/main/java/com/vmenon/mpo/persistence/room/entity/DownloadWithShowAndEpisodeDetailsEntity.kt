package com.vmenon.mpo.persistence.room.entity

import androidx.room.Embedded

data class DownloadWithShowAndEpisodeDetailsEntity(
    @Embedded val download: DownloadEntity,
    @Embedded val episode: EpisodeDetailsEntity,
    @Embedded val show: ShowDetailsEntity
)
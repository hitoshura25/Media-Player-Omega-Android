package com.vmenon.mpo.persistence.room.entity

import androidx.room.Embedded

data class EpisodeWithShowDetailsEntity(
    @Embedded
    val episode: EpisodeEntity,
    @Embedded
    val showDetails: ShowDetailsEntity
)
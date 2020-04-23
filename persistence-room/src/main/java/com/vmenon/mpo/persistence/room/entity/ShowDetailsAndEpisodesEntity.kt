package com.vmenon.mpo.persistence.room.entity

data class ShowDetailsAndEpisodesEntity(
    val showDetails: ShowDetailsEntity,
    val episodes: List<EpisodeDetailsEntity>
)
package com.vmenon.mpo.model

import androidx.room.Embedded

data class EpisodeWithShowDetailsModel(
    @Embedded
    val episode: EpisodeModel,
    @Embedded
    val showDetails: ShowDetailsModel
)
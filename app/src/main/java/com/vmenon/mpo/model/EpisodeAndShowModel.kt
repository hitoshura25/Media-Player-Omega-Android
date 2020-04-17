package com.vmenon.mpo.model

import androidx.room.Embedded

data class EpisodeAndShowModel(
    @Embedded
    val episode: EpisodeModel,
    @Embedded
    val show: ShowModel
)
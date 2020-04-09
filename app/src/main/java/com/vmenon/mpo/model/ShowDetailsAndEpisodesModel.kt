package com.vmenon.mpo.model

data class ShowDetailsAndEpisodesModel(
    val showDetails: ShowDetailsModel,
    val episodes: List<EpisodeModel>,
    val showDescription: String
)
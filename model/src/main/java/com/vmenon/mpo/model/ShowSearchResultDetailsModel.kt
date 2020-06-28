package com.vmenon.mpo.model

data class ShowSearchResultDetailsModel(
    val show: ShowSearchResultModel,
    val episodes: List<ShowSearchResultEpisodeModel>,
    val subscribed: Boolean
)
package com.vmenon.mpo.search.domain

data class ShowSearchResultDetailsModel(
    val show: ShowSearchResultModel,
    val episodes: List<ShowSearchResultEpisodeModel>,
    val subscribed: Boolean
)
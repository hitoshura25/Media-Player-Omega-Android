package com.vmenom.mpo.model

data class ShowSearchResultDetailsModel(
    val show: ShowSearchResultModel,
    val episodes: List<ShowSearchResultEpisodeModel>
)
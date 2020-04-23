package com.vmenom.mpo.model

data class ShowSearchResultModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val artWorkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val description: String
)
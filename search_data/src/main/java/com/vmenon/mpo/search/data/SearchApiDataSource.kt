package com.vmenon.mpo.search.data

import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails

interface SearchApiDataSource {
    suspend fun searchShows(searchTerm: String): List<Show>
    suspend fun getShowDetails(feedUrl: String, maxEpisodes: Int): ShowDetails
}
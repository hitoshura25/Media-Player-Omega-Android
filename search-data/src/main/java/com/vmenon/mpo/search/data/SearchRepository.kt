package com.vmenon.mpo.search.data

import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.domain.ShowSearchService
import java.lang.IllegalArgumentException

class SearchRepository(
    private val apiDataSource: SearchApiDataSource,
    private val cacheDataSource: SearchCacheDataSource
) : ShowSearchService {

    override suspend fun getShowDetails(showSearchResultId: Long): ShowSearchResultDetailsModel {
        val showSearchResult = cacheDataSource.getSearchResultById(showSearchResultId)
        if (showSearchResult == null) {
            throw IllegalArgumentException("No show search result with id $showSearchResultId exists")
        } else {
            val showDetails = apiDataSource.getShowDetails(
                showSearchResult.feedUrl,
                10
            )

            return ShowSearchResultDetailsModel(
                show = showSearchResult,
                episodes = showDetails.episodes.map { episode ->
                    ShowSearchResultEpisodeModel(
                        name = episode.name,
                        artworkUrl = episode.artworkUrl,
                        description = episode.description,
                        downloadUrl = episode.downloadUrl,
                        length = episode.length,
                        published = episode.published,
                        type = episode.type
                    )
                },
                subscribed = false
            )
        }
    }

    override suspend fun searchShows(keyword: String): List<ShowSearchResultModel>? {
        val showSearchResults = apiDataSource.searchShows(keyword).map { show ->
            ShowSearchResultModel(
                author = show.author,
                artworkUrl = show.artworkUrl,
                feedUrl = show.feedUrl ?: "",
                genres = show.genres,
                name = show.name,
                description = ""
            )
        }
        cacheDataSource.store(keyword, showSearchResults)
        return cacheDataSource.loadSearchResultsForTerm(keyword)
    }
}
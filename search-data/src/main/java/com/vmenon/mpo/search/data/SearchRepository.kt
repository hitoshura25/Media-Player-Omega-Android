package com.vmenon.mpo.search.data

import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.domain.ShowSearchService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository(
    private val apiDataSource: SearchApiDataSource,
    private val cacheDataSource: SearchCacheDataSource
) : ShowSearchService {
    suspend fun getShowSearchResultsForTerm(term: String): List<ShowSearchResultModel>? {
        return cacheDataSource.loadSearchResultsForTerm(term)
    }

    override suspend fun getShowDetails(showSearchResultId: Long): Flow<ResultState<ShowSearchResultDetailsModel>> =
        flow {
            emit(LoadingState)
            val showSearchResult = cacheDataSource.getSearchResultById(showSearchResultId)
            if (showSearchResult == null) {
                emit(ErrorState)
            } else {
                val showDetails = apiDataSource.getShowDetails(
                    showSearchResult.feedUrl,
                    10
                )

                emit(
                    SuccessState(
                        ShowSearchResultDetailsModel(
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
                    )
                )
            }
        }

    override suspend fun searchShows(keyword: String): Flow<ResultState<List<ShowSearchResultModel>>> =
        flow {
            emit(LoadingState)
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
            cacheDataSource.loadSearchResultsForTerm(keyword)?.let { results ->
                emit(SuccessState(results))
            }
        }
}
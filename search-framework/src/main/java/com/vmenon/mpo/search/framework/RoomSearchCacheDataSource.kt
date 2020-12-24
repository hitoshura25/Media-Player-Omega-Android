package com.vmenon.mpo.search.framework

import com.vmenon.mpo.persistence.room.base.entity.BaseEntity
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.entity.ShowSearchEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity
import com.vmenon.mpo.search.data.SearchCacheDataSource
import com.vmenon.mpo.search.domain.ShowSearchResultModel

class RoomSearchCacheDataSource(
    private val searchResultDao: ShowSearchResultDao
) : SearchCacheDataSource {
    override suspend fun loadSearchResultsForTerm(
        searchTerm: String
    ): List<ShowSearchResultModel>? =
        searchResultDao.getBySearchTerm(searchTerm)?.map { entity ->
            ShowSearchResultModel(
                id = entity.showSearchResultsId,
                name = entity.showName,
                genres = entity.genres,
                feedUrl = entity.feedUrl,
                author = entity.author,
                artworkUrl = entity.showArtworkUrl,
                description = entity.showDescription
            )
        }

    override suspend fun getSearchResultById(
        id: Long
    ): ShowSearchResultModel? = searchResultDao.getSearchResultById(id)?.let { entity ->
        ShowSearchResultModel(
            id = entity.showSearchResultsId,
            name = entity.showName,
            genres = entity.genres,
            feedUrl = entity.feedUrl,
            author = entity.author,
            artworkUrl = entity.showArtworkUrl,
            description = entity.showDescription
        )
    }

    override suspend fun store(
        searchTerm: String,
        results: List<ShowSearchResultModel>
    ) {
        val showSearch = searchResultDao.getSearchForTerm(searchTerm)
        val showSearchId: Long
        if (showSearch != null) {
            showSearchId = showSearch.showSearchId
            searchResultDao.deleteResultsForSearch(showSearch.showSearchId)
        } else {
            val newSearch =
                ShowSearchEntity(
                    showSearchId = BaseEntity.UNSAVED_ID,
                    searchTerm = searchTerm
                )
            showSearchId = searchResultDao.save(newSearch)
        }
        val searchResults = ArrayList<ShowSearchResultsEntity>()
        results.forEach { show ->
            show.feedUrl.let {
                searchResults.add(
                    ShowSearchResultsEntity(
                        showName = show.name,
                        showArtworkUrl = show.artworkUrl,
                        author = show.author,
                        feedUrl = it,
                        genres = show.genres,
                        showDescription = "",
                        lastEpisodePublished = 0L,
                        lastUpdate = 0L,
                        isSubscribed = false,
                        showSearchResultsId = 0L,
                        showSearchResultsSearchId = showSearchId
                    )
                )
            }
        }
        searchResultDao.save(searchResults).forEachIndexed { index, id ->
            searchResults[index] = searchResults[index].copy(showSearchResultsId = id)
        }
    }
}
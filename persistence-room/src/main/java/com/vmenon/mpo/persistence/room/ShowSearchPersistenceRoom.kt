package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.entity.ShowSearchEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity
import com.vmenon.mpo.search.persistence.ShowSearchPersistence

class ShowSearchPersistenceRoom(
    private val showSearchResultDao: ShowSearchResultDao
) : ShowSearchPersistence {
    override suspend fun getBySearchTermOrderedByName(searchTerm: String): List<ShowSearchResultModel> =
        showSearchResultDao.getBySearchTerm(searchTerm)?.map { it.toModel() } ?: emptyList()

    override suspend fun getSearchResultById(id: Long): ShowSearchResultModel? {
        return showSearchResultDao.getSearchResultById(id)?.toModel()
    }

    override suspend fun save(results: SearchResultsModel) {
        val showSearch = showSearchResultDao.getSearchForTerm(results.searchTerm)
        val showSearchId: Long
        if (showSearch != null) {
            showSearchId = showSearch.showSearchId
            showSearchResultDao.deleteResultsForSearch(showSearch.showSearchId)
        } else {
            val newSearch =
                ShowSearchEntity(
                    showSearchId = BaseEntity.UNSAVED_ID,
                    searchTerm = results.searchTerm
                )
            showSearchId = showSearchResultDao.save(newSearch)
        }
        val searchResults = ArrayList<ShowSearchResultsEntity>()
        results.shows.forEach { show ->
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
        showSearchResultDao.save(searchResults).forEachIndexed { index, id ->
            searchResults[index] = searchResults[index].copy(showSearchResultsId = id)
        }
    }
}
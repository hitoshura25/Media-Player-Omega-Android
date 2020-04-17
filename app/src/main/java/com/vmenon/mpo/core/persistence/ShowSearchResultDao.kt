package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.ShowSearchModel
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface ShowSearchResultDao {
    @Query(
        """
        SELECT showSearchResults.* 
        FROM showSearchResults 
        INNER JOIN showSearch ON showSearchResults.showSearchResultsSearchId = showSearch.showSearchId 
        WHERE showSearch.searchTerm = :searchTerm
        ORDER BY showSearchResults.showName
        """
    )
    fun loadSearchResults(searchTerm: String): Flowable<List<ShowSearchResultsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSearch(search: ShowSearchModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSearchResults(searchResults: List<ShowSearchResultsModel>): List<Long>

    @Query("SELECT * FROM showSearch WHERE searchTerm=:searchTerm")
    fun getSearchForTerm(searchTerm: String): Maybe<ShowSearchModel>

    @Query("SELECT * FROM showSearchResults WHERE showSearchResultsId = :id")
    fun getSearchResultById(id: Long): Flowable<ShowSearchResultsModel>

    @Query("DELETE FROM showSearch WHERE showSearchId = :id")
    fun deleteShowSearch(id: Long)

    @Query("DELETE FROM showSearchResults where showSearchResultsSearchId=:searchId")
    fun deleteResultsForSearch(searchId: Long)
}
package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.ShowSearchModel
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.Flowable

@Dao
interface ShowSearchResultDao {
    @Query(
        """
        SELECT showSearchResults.* 
        FROM showSearchResults 
        INNER JOIN showSearch ON showSearchId = showSearch.id 
        WHERE showSearch.searchTerm = :searchTerm
        """
    )
    fun loadSearchResults(searchTerm: String): Flowable<List<ShowSearchResultsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSearch(search: ShowSearchModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSearchResults(searchResults: List<ShowSearchResultsModel>): List<Long>

    @Query("SELECT * FROM showSearch WHERE searchTerm=:searchTerm")
    fun getSearchForTerm(searchTerm: String): ShowSearchModel?

    @Query("SELECT * FROM showSearchResults WHERE id = :id")
    fun getSearchResultById(id: Long): ShowSearchResultsModel

    @Query("DELETE FROM showSearch WHERE id = :id")
    fun deleteShowSearch(id: Long)

    @Query("DELETE FROM showSearchResults where showSearchId=:searchId")
    fun deleteResultsForSearch(searchId: Long)
}
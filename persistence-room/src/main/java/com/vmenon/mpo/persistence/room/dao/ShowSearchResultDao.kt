package com.vmenon.mpo.persistence.room.dao

import androidx.room.*
import com.vmenon.mpo.persistence.room.entity.ShowSearchEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity
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
    fun getBySearchTermOrderedByName(searchTerm: String): Flowable<List<ShowSearchResultsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(search: ShowSearchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(searchResults: List<ShowSearchResultsEntity>): List<Long>

    @Query("SELECT * FROM showSearch WHERE searchTerm=:searchTerm")
    fun getSearchForTerm(searchTerm: String): Maybe<ShowSearchEntity>

    @Query("SELECT * FROM showSearchResults WHERE showSearchResultsId = :id")
    fun getSearchResultById(id: Long): Flowable<ShowSearchResultsEntity>

    @Query("DELETE FROM showSearchResults where showSearchResultsSearchId=:searchId")
    fun deleteResultsForSearch(searchId: Long)
}
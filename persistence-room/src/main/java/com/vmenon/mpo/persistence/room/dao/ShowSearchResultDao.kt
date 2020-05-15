package com.vmenon.mpo.persistence.room.dao

import androidx.room.*
import com.vmenon.mpo.persistence.room.entity.ShowSearchEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity
import kotlinx.coroutines.flow.Flow

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
    fun getBySearchTerm(searchTerm: String): Flow<List<ShowSearchResultsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(search: ShowSearchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(searchResults: List<ShowSearchResultsEntity>): List<Long>

    @Query("SELECT * FROM showSearch WHERE searchTerm=:searchTerm")
    suspend fun getSearchForTerm(searchTerm: String): ShowSearchEntity?

    @Query("SELECT * FROM showSearchResults WHERE showSearchResultsId = :id")
    fun getSearchResultById(id: Long): Flow<ShowSearchResultsEntity>

    @Query("DELETE FROM showSearchResults where showSearchResultsSearchId=:searchId")
    suspend fun deleteResultsForSearch(searchId: Long)
}
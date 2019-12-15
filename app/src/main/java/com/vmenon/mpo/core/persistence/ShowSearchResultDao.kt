package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.Flowable

@Dao
interface ShowSearchResultDao {
    @Query("SELECT * FROM showSearchResults")
    fun load(): Flowable<List<ShowSearchResultsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(searchResult: ShowSearchResultsModel)

    @Query("SELECT * FROM showSearchResults where id = :id")
    fun getById(id: Long): ShowSearchResultsModel

    @Query("DELETE FROM showSearchResults")
    fun deleteAll()
}
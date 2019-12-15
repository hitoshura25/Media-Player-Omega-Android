package com.vmenon.mpo.core.persistence

import androidx.room.*

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.SubscribedShowModel
import com.vmenon.mpo.model.ShowSearchResultsModel

@Database(entities = [SubscribedShowModel::class, EpisodeModel::class, ShowSearchResultsModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class MPODatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun showSearchResultsDao(): ShowSearchResultDao
}

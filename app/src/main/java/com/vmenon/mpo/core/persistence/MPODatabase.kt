package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.*

@Database(
    entities = [
        ShowModel::class,
        EpisodeModel::class,
        ShowSearchModel::class,
        ShowSearchResultsModel::class,
        DownloadModel::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MPODatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun showSearchResultsDao(): ShowSearchResultDao
    abstract fun downloadDao(): DownloadDao
}

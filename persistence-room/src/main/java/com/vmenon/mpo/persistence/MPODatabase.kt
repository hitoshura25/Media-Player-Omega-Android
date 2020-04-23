package com.vmenon.mpo.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vmenon.mpo.persistence.room.entity.*
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao

@Database(
    entities = [
        ShowEntity::class,
        EpisodeEntity::class,
        ShowSearchEntity::class,
        ShowSearchResultsEntity::class,
        DownloadEntity::class
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

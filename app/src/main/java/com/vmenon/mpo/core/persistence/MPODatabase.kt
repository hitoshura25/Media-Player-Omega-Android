package com.vmenon.mpo.core.persistence

import androidx.room.*

import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show

@Database(entities = [Show::class, Episode::class], version = 1)
@TypeConverters(Converters::class)
abstract class MPODatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
    abstract fun episodeDao(): EpisodeDao
}

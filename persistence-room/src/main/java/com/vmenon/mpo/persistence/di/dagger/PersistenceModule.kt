package com.vmenon.mpo.persistence.di.dagger

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.persistence.room.*
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.shows.persistence.ShowPersistence
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PersistenceModule {

    @Provides
    @Singleton
    fun provideShowPersistence(showDao: ShowDao): ShowPersistence = ShowPersistenceRoom(showDao)

    @Provides
    @Singleton
    fun provideEpisodePersistence(episodeDao: EpisodeDao): EpisodePersistence =
        EpisodePersistenceRoom(episodeDao)

    @Provides
    @Singleton
    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext, MPODatabase::class.java,
            "mpo-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideShowDao(database: MPODatabase): ShowDao {
        return database.showDao()
    }

    @Provides
    @Singleton
    fun provideEpisodeDao(database: MPODatabase): EpisodeDao {
        return database.episodeDao()
    }

    @Provides
    @Singleton
    fun provideDownloadDao(database: MPODatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    @Singleton
    fun provideShowSearchResultDao(database: MPODatabase): ShowSearchResultDao =
        database.showSearchResultsDao()
}
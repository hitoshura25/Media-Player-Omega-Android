package com.vmenon.mpo.persistence.di.dagger

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.persistence.room.MPODatabase
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.migrations.Migrations
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PersistenceModule {
    @Provides
    @Singleton
    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            MPODatabase::class.java,
            "mpo-database"
        ).addMigrations(Migrations.MIGRATION_1_2)
            .build()
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
package com.vmenon.mpo.di

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.core.persistence.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    internal fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext, MPODatabase::class.java,
            "mpo-database"
        ).build()
    }

    @Provides
    @Singleton
    internal fun provideShowDao(mpDatabase: MPODatabase): ShowDao {
        return mpDatabase.showDao()
    }

    @Provides
    @Singleton
    internal fun provideEpisodeDao(mpDatabase: MPODatabase): EpisodeDao {
        return mpDatabase.episodeDao()
    }

    @Provides
    @Singleton
    internal fun provideDownloadDao(mpDatabase: MPODatabase): DownloadDao {
        return mpDatabase.downloadDao()
    }

    @Provides
    @Singleton
    internal fun provideShowSearchResultsDao(mpDatabase: MPODatabase): ShowSearchResultDao {
        return mpDatabase.showSearchResultsDao()
    }
}
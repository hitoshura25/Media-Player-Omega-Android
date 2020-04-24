package com.vmenon.mpo.persistence.room.di

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.persistence.room.MPODatabase
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import dagger.Module
import dagger.Provides

@Module
class RoomModule {
    @Provides
    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext, MPODatabase::class.java,
            "mpo-database"
        ).build()
    }

    @Provides
    fun provideShowDao(mpDatabase: MPODatabase): ShowDao {
        return mpDatabase.showDao()
    }

    @Provides
    fun provideEpisodeDao(mpDatabase: MPODatabase): EpisodeDao {
        return mpDatabase.episodeDao()
    }

    @Provides
    fun provideDownloadDao(mpDatabase: MPODatabase): DownloadDao {
        return mpDatabase.downloadDao()
    }

    @Provides
    fun provideShowSearchResultsDao(mpDatabase: MPODatabase): ShowSearchResultDao {
        return mpDatabase.showSearchResultsDao()
    }
}
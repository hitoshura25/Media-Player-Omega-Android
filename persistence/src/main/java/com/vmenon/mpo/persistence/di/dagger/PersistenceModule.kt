package com.vmenon.mpo.persistence.di.dagger

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.persistence.ShowSearchPersistence
import com.vmenon.mpo.persistence.room.MPODatabase
import com.vmenon.mpo.persistence.room.ShowSearchPersistenceRoom
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import dagger.Module
import dagger.Provides

@Module
class PersistenceModule(application: Application) {
    private val mpDatabase: MPODatabase by lazy { provideMPODatabase(application) }

    @Provides
    fun provideShowSearchPersistence(): ShowSearchPersistence =
        ShowSearchPersistenceRoom(provideShowSearchResultsDao())

    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext, MPODatabase::class.java,
            "mpo-database"
        ).build()
    }

    @Provides
    fun provideShowDao(): ShowDao {
        return mpDatabase.showDao()
    }

    @Provides
    fun provideEpisodeDao(): EpisodeDao {
        return mpDatabase.episodeDao()
    }

    @Provides
    fun provideDownloadDao(): DownloadDao {
        return mpDatabase.downloadDao()
    }

    @Provides
    fun provideShowSearchResultsDao(): ShowSearchResultDao {
        return mpDatabase.showSearchResultsDao()
    }
}
package com.vmenon.mpo.persistence.di.dagger

import com.vmenon.mpo.persistence.room.MPODatabase
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import dagger.Module
import dagger.Provides

@Module
object PersistenceModule {
    @Provides
    @PersistenceScope
    fun provideShowDao(database: MPODatabase): ShowDao {
        return database.showDao()
    }

    @Provides
    @PersistenceScope
    fun provideEpisodeDao(database: MPODatabase): EpisodeDao {
        return database.episodeDao()
    }

    @Provides
    @PersistenceScope
    fun provideDownloadDao(database: MPODatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    @PersistenceScope
    fun provideShowSearchResultDao(database: MPODatabase): ShowSearchResultDao =
        database.showSearchResultsDao()
}
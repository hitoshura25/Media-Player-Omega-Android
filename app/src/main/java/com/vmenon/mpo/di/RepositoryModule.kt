package com.vmenon.mpo.di

import android.app.Application
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideShowSearchRepository(
        service: MediaPlayerOmegaService,
        showSearchResultDao: ShowSearchResultDao
    ): com.vmenon.mpo.repository.ShowSearchRepository {
        return com.vmenon.mpo.repository.ShowSearchRepository(
            service,
            showSearchResultDao
        )
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(
        application: Application,
        downloadDao: DownloadDao,
        episodeDao: EpisodeDao,
        showDao: ShowDao
    ) =
        com.vmenon.mpo.repository.DownloadRepository(
            application.applicationContext,
            downloadDao,
            episodeDao,
            showDao
        )

    @Provides
    @Singleton
    fun provideEpisodeRepository(episodeDao: EpisodeDao) =
        com.vmenon.mpo.repository.EpisodeRepository(episodeDao)

    @Provides
    @Singleton
    fun provideShowRepository(showDao: ShowDao, service: MediaPlayerOmegaService) =
        com.vmenon.mpo.repository.ShowRepository(showDao, service)
}
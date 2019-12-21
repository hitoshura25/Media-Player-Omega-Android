package com.vmenon.mpo.di

import com.vmenon.mpo.core.persistence.DownloadDao
import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.core.persistence.ShowDao
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.core.repository.MPORepository
import com.vmenon.mpo.core.repository.ShowSearchRepository
import com.vmenon.mpo.service.MediaPlayerOmegaService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    internal fun provideMPORepository(
        service: MediaPlayerOmegaService,
        showDao: ShowDao, episodeDao: EpisodeDao
    ): MPORepository {
        return MPORepository(
            service,
            showDao,
            episodeDao
        )
    }

    @Provides
    @Singleton
    internal fun provideShowSearchRepository(
        service: MediaPlayerOmegaService,
        showSearchResultDao: ShowSearchResultDao
    ): ShowSearchRepository {
        return ShowSearchRepository(
            service,
            showSearchResultDao
        )
    }

    @Provides
    @Singleton
    internal fun provideDownloadRepository(downloadDao: DownloadDao) =
        DownloadRepository(downloadDao)
}
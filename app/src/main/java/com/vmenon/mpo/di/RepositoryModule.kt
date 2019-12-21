package com.vmenon.mpo.di

import com.vmenon.mpo.core.persistence.DownloadDao
import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.core.persistence.ShowDao
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.core.repository.*
import com.vmenon.mpo.service.MediaPlayerOmegaService
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
    ): ShowSearchRepository {
        return ShowSearchRepository(
            service,
            showSearchResultDao
        )
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(downloadDao: DownloadDao) =
        DownloadRepository(downloadDao)

    @Provides
    @Singleton
    fun provideEpisodeRepository(episodeDao: EpisodeDao) =
        EpisodeRepository(episodeDao)

    @Provides
    @Singleton
    fun provideShowRepository(showDao: ShowDao) =
        ShowRepository(showDao)
}
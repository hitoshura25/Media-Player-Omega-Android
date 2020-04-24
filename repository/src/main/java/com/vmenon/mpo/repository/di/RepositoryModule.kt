package com.vmenon.mpo.repository.di

import android.app.Application
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.di.RoomModule
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import com.vmenon.mpo.repository.ShowSearchRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RoomModule::class])
class RepositoryModule {
    @Provides
    @Singleton
    fun provideShowSearchRepository(
        api: MediaPlayerOmegaApi,
        showSearchResultDao: ShowSearchResultDao
    ): ShowSearchRepository {
        return ShowSearchRepository(
            api,
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
        DownloadRepository(
            application.applicationContext,
            downloadDao,
            episodeDao,
            showDao
        )

    @Provides
    @Singleton
    fun provideEpisodeRepository(episodeDao: EpisodeDao) =
        EpisodeRepository(episodeDao)

    @Provides
    @Singleton
    fun provideShowRepository(showDao: ShowDao, api: MediaPlayerOmegaApi) =
        ShowRepository(showDao, api)
}
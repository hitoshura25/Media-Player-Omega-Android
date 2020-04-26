package com.vmenon.mpo.repository.di.dagger

import android.app.Application
import com.vmenon.mpo.api.di.dagger.ApiComponent
import com.vmenon.mpo.persistence.room.di.dagger.RoomModule
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import com.vmenon.mpo.repository.ShowSearchRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule(application: Application, apiComponent: ApiComponent) {
    private val roomModule: RoomModule by lazy { RoomModule(application) }
    private val searchRepository: ShowSearchRepository by lazy {
        ShowSearchRepository(
            apiComponent.api(),
            roomModule.provideShowSearchResultsDao()
        )
    }

    private val showRepository: ShowRepository by lazy {
        ShowRepository(roomModule.provideShowDao(), apiComponent.api())
    }

    private val episodeRepository: EpisodeRepository by lazy {
        EpisodeRepository(roomModule.provideEpisodeDao())
    }

    private val downloadRepository: DownloadRepository by lazy {
        DownloadRepository(
            context = application,
            downloadDao = roomModule.provideDownloadDao(),
            episodeDao = roomModule.provideEpisodeDao(),
            showDao = roomModule.provideShowDao()
        )
    }

    @Provides
    fun provideShowSearchRepository(): ShowSearchRepository = searchRepository

    @Provides
    fun provideDownloadRepository(): DownloadRepository = downloadRepository

    @Provides
    fun provideEpisodeRepository(): EpisodeRepository = episodeRepository

    @Provides
    fun provideShowRepository(): ShowRepository = showRepository
}
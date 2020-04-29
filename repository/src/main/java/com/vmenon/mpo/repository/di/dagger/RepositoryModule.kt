package com.vmenon.mpo.repository.di.dagger

import android.app.Application
import com.vmenon.mpo.api.di.dagger.ApiComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceModule
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import com.vmenon.mpo.repository.ShowSearchRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule(application: Application, apiComponent: ApiComponent) {
    private val roomModule: PersistenceModule by lazy { PersistenceModule(application) }
    private val searchRepository: ShowSearchRepository by lazy {
        ShowSearchRepository(
            apiComponent.api(),
            roomModule.provideShowSearchPersistence()
        )
    }

    private val showRepository: ShowRepository by lazy {
        ShowRepository(roomModule.provideShowPersistence(), apiComponent.api())
    }

    private val episodeRepository: EpisodeRepository by lazy {
        EpisodeRepository(roomModule.provideEpisodePersistence())
    }

    private val downloadRepository: DownloadRepository by lazy {
        DownloadRepository(
            context = application,
            downloadPersistence = roomModule.provideDownloadPersistence(),
            episodePersistence = roomModule.provideEpisodePersistence(),
            showPersistence = roomModule.provideShowPersistence()
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
package com.vmenon.mpo.repository.di.dagger

import android.app.Application
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.persistence.DownloadPersistence
import com.vmenon.mpo.persistence.EpisodePersistence
import com.vmenon.mpo.persistence.ShowPersistence
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideDownloadRepository(
        application: Application,
        downloadPersistence: DownloadPersistence,
        episodePersistence: EpisodePersistence,
        showPersistence: ShowPersistence
    ): DownloadRepository = DownloadRepository(
        context = application,
        downloadPersistence = downloadPersistence,
        episodePersistence = episodePersistence,
        showPersistence = showPersistence
    )

    @Provides
    fun provideEpisodeRepository(
        episodePersistence: EpisodePersistence
    ): EpisodeRepository = EpisodeRepository(episodePersistence)

    @Provides
    fun provideShowRepository(
        showPersistence: ShowPersistence,
        api: MediaPlayerOmegaApi
    ): ShowRepository = ShowRepository(
        showPersistence,
        api
    )
}
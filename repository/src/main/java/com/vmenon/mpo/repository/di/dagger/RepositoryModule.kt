package com.vmenon.mpo.repository.di.dagger

import android.app.Application
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.download.repository.impl.DownloadRepositoryImpl
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import com.vmenon.mpo.search.repository.ShowSearchRepository
import com.vmenon.mpo.search.repository.impl.EpisodeRepositoryImpl
import com.vmenon.mpo.search.repository.impl.ShowSearchRepositoryImpl
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.shows.persistence.ShowPersistence
import com.vmenon.mpo.shows.repository.EpisodeRepository
import com.vmenon.mpo.shows.repository.ShowRepository
import com.vmenon.mpo.shows.repository.impl.ShowRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideDownloadRepository(
        application: Application,
        downloadPersistence: com.vmenon.mpo.downloads.persistence.DownloadPersistence,
        episodePersistence: EpisodePersistence,
        showPersistence: ShowPersistence
    ): DownloadRepository = DownloadRepositoryImpl(
        context = application,
        downloadPersistence = downloadPersistence,
        episodePersistence = episodePersistence,
        showPersistence = showPersistence
    )

    @Provides
    fun provideEpisodeRepository(
        episodePersistence: EpisodePersistence
    ): EpisodeRepository = EpisodeRepositoryImpl(episodePersistence)

    @Provides
    fun provideShowRepository(
        showPersistence: ShowPersistence,
        api: MediaPlayerOmegaApi
    ): ShowRepository = ShowRepositoryImpl(
        showPersistence,
        api
    )

    @Provides
    fun showSearchRepository(
        api: MediaPlayerOmegaApi,
        showSearchPersistence: ShowSearchPersistence
    ): ShowSearchRepository =
        ShowSearchRepositoryImpl(
            api,
            showSearchPersistence
        )
}
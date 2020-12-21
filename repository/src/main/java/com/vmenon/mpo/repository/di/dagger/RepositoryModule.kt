package com.vmenon.mpo.repository.di.dagger

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.search.repository.impl.EpisodeRepositoryImpl
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
}
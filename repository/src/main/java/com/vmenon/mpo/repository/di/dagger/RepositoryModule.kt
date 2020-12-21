package com.vmenon.mpo.repository.di.dagger

import com.vmenon.mpo.search.repository.impl.EpisodeRepositoryImpl
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.shows.repository.EpisodeRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideEpisodeRepository(
        episodePersistence: EpisodePersistence
    ): EpisodeRepository = EpisodeRepositoryImpl(episodePersistence)
}
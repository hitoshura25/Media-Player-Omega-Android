package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import com.vmenon.mpo.search.repository.ShowSearchRepository
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
    fun showSearchRepository(
        api: MediaPlayerOmegaApi,
        showSearchPersistence: ShowSearchPersistence
    ): ShowSearchRepository =
        ShowSearchRepository(api, showSearchPersistence)
}
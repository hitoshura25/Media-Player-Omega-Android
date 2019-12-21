package com.vmenon.mpo.di

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.viewmodel.HomeViewModel
import com.vmenon.mpo.viewmodel.LibraryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(userViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LibraryViewModel::class)
    abstract fun bindLibraryViewModel(libraryViewModel: LibraryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeDetailsViewModel::class)
    abstract fun bindEpisodeDetailsViewModel(
        episodeDetailsViewModel: EpisodeDetailsViewModel
    ): ViewModel
}
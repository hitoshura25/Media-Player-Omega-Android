package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.library.view.fragment.EpisodeDetailsFragment
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.library.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import com.vmenon.mpo.library.worker.UpdateAllShowsWorker
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [LibraryModule::class])
@LibraryScope
interface LibraryComponent {

    fun inject(fragment: LibraryFragment)
    fun inject(viewModel: LibraryViewModel)

    fun inject(fragment: EpisodeDetailsFragment)
    fun inject(viewModel: EpisodeDetailsViewModel)

    fun inject(fragment: SubscribedShowsFragment)
    fun inject(viewModel: SubscribedShowsViewModel)

    fun inject(worker: UpdateAllShowsWorker)
}
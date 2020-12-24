package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.library.view.activity.EpisodeDetailsActivity
import com.vmenon.mpo.library.view.activity.LibraryActivity
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.library.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import dagger.Subcomponent

@Subcomponent
@LibraryScope
interface LibraryComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LibraryComponent
    }

    fun inject(activity: LibraryActivity)
    fun inject(viewModel: LibraryViewModel)

    fun inject(activity: EpisodeDetailsActivity)
    fun inject(viewModel: EpisodeDetailsViewModel)

    fun inject(fragment: SubscribedShowsFragment)
    fun inject(viewModel: SubscribedShowsViewModel)
}
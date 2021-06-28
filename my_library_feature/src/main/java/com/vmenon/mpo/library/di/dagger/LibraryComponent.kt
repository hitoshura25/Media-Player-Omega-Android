package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.downloads.framework.di.dagger.DownloadsFrameworkComponent
import com.vmenon.mpo.library.view.fragment.EpisodeDetailsFragment
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.library.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import com.vmenon.mpo.library.worker.UpdateAllShowsWorker
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponent
import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
        DownloadsFrameworkComponent::class,
        PlayerFrameworkComponent::class,
        LibraryFrameworkComponent::class
    ],
    modules = [LibraryModule::class]
)
@LibraryScope
interface LibraryComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun downloadsFrameworkComponent(component: DownloadsFrameworkComponent): Builder
        fun playerFrameworkComponent(component: PlayerFrameworkComponent): Builder
        fun libraryFrameworkComponent(component: LibraryFrameworkComponent): Builder
        fun build(): LibraryComponent
    }

    fun inject(fragment: LibraryFragment)
    fun inject(viewModel: LibraryViewModel)

    fun inject(fragment: EpisodeDetailsFragment)
    fun inject(viewModel: EpisodeDetailsViewModel)

    fun inject(fragment: SubscribedShowsFragment)
    fun inject(viewModel: SubscribedShowsViewModel)

    fun inject(worker: UpdateAllShowsWorker)
}


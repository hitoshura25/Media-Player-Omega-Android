package com.vmenon.mpo.my_library.presentation.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.my_library.presentation.fragment.EpisodeDetailsFragment
import com.vmenon.mpo.my_library.presentation.fragment.LibraryFragment
import com.vmenon.mpo.my_library.presentation.fragment.SubscribedShowsFragment
import com.vmenon.mpo.my_library.presentation.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.my_library.presentation.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.my_library.presentation.viewmodel.LibraryViewModel
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponent
import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
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
}


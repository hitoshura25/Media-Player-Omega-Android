package com.vmenon.mpo.player.presentation.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponent
import com.vmenon.mpo.player.presentation.fragment.MediaPlayerFragment
import com.vmenon.mpo.player.presentation.viewmodel.MediaPlayerViewModel
import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
        PlayerFrameworkComponent::class
    ],
    modules = [PlayerModule::class]
)
@PlayerScope
interface PlayerComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun playerFrameworkComponent(component: PlayerFrameworkComponent): Builder
        fun build(): PlayerComponent
    }

    fun inject(fragment: MediaPlayerFragment)
    fun inject(viewModel: MediaPlayerViewModel)
}
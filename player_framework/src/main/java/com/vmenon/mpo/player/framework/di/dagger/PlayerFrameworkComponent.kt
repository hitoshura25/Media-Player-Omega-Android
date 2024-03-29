package com.vmenon.mpo.player.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.NavigationParamsConverter
import com.vmenon.mpo.player.domain.PlaybackStateTracker
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.MPOPlayer
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [PlayerFrameworkModule::class]
)
@PlayerFrameworkScope
interface PlayerFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun playerFrameworkModule(module: PlayerFrameworkModule): Builder
        fun build(): PlayerFrameworkComponent
    }

    fun inject(service: MPOMediaBrowserService)
    fun player(): MPOPlayer
    fun playerEngine(): MediaPlayerEngine
    fun navigationParamsConverter(): NavigationParamsConverter
    fun playbackStateTracker(): PlaybackStateTracker
}
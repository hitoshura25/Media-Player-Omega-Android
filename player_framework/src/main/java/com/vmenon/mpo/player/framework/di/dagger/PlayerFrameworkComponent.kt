package com.vmenon.mpo.player.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.MPOPlayer
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class, AppComponent::class],
    modules = [PlayerFrameworkModule::class]
)
@PlayerFrameworkScope
interface PlayerFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun appComponent(component: AppComponent): Builder
        fun build(): PlayerFrameworkComponent
    }

    fun inject(service: MPOMediaBrowserService)
    fun episodePlayerRequestMapper(): PlayerRequestMapper<EpisodeModel>
    fun player(): MPOPlayer
    fun playerEngine(): MediaPlayerEngine
}
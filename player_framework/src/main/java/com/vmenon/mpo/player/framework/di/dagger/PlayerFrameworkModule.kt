package com.vmenon.mpo.player.framework.di.dagger

import android.content.Context
import androidx.core.content.ContextCompat
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.EpisodeModelPlayerRequestMapper
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.view.R
import dagger.Module
import dagger.Provides

@Module
object PlayerFrameworkModule {
    @Provides
    @PlayerFrameworkScope
    fun provideEpisodeRequestMapper(): PlayerRequestMapper<EpisodeModel> =
        EpisodeModelPlayerRequestMapper()

    @Provides
    @PlayerFrameworkScope
    fun providePlayerEngine(commonFrameworkComponent: CommonFrameworkComponent): MediaPlayerEngine =
        AndroidMediaBrowserServicePlayerEngine(
            commonFrameworkComponent.systemFrameworkComponent().application()
        )

    @Provides
    @PlayerFrameworkScope
    fun providePlayer(commonFrameworkComponent: CommonFrameworkComponent): MPOPlayer =
        MPOExoPlayer(commonFrameworkComponent.systemFrameworkComponent().application())

    @Provides
    @PlayerFrameworkScope
    fun providesMPOMediaBrowserServiceConfiguration(
        commonFrameworkComponent: CommonFrameworkComponent,
        player: MPOPlayer,
        playerDestination: NavigationDestination<PlayerNavigationLocation>,
        navigationController: NavigationController
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        player,
        { request: PlaybackMediaRequest?, context: Context ->
            navigationController.createNavigationRequest(
                context,
                PlayerNavigationParams(request),
                playerDestination
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(
                commonFrameworkComponent.systemFrameworkComponent().application(),
                R.color.colorPrimary
            )
        })
}
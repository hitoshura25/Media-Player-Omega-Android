package com.vmenon.mpo.player.framework.di.dagger

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.player.*
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.DefaultNavigationParamsConverter
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
    fun providePlayerEngine(application: Application): MediaPlayerEngine =
        AndroidMediaBrowserServicePlayerEngine(
            application
        )

    @Provides
    @PlayerFrameworkScope
    fun providePlayer(application: Application): MPOPlayer =
        MPOExoPlayer(application)

    @Provides
    @PlayerFrameworkScope
    fun providesMPOMediaBrowserServiceConfiguration(
        application: Application,
        playerDestination: NavigationDestination<PlayerNavigationLocation>,
        navigationController: NavigationController
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        { request: PlaybackMediaRequest?, context: Context ->
            navigationController.createNavigationRequest(
                context,
                PlayerNavigationParams(
                    request?.let {
                        Media(
                            mediaId = request.media.mediaId,
                            mediaSource = FileMediaSource(request.mediaFile),
                            title = request.media.title,
                            album = request.media.album,
                            genres = request.media.genres,
                            artworkUrl = request.media.artworkUrl,
                            author = request.media.author
                        )
                    }
                ),
                playerDestination
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(
                application,
                R.color.colorPrimary
            )
        })

    @Provides
    @PlayerFrameworkScope
    fun provideNavigationParamsConverters(): NavigationParamsConverter =
        DefaultNavigationParamsConverter()
}
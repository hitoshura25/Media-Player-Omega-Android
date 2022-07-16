package com.vmenon.mpo.player.framework.di.dagger

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.player.FileMediaSource
import com.vmenon.mpo.navigation.domain.player.Media
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.NavigationParamsConverter
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlaybackStateTracker
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.DefaultNavigationParamsConverter
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.view.R
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors

@Module
class PlayerFrameworkModule(private val playbackStateTracker: PlaybackStateTracker) {
    @Provides
    @PlayerFrameworkScope
    fun providePlaybackStateTracker() = playbackStateTracker

    @Provides
    @PlayerFrameworkScope
    fun providePlayerEngine(application: Application): MediaPlayerEngine =
        AndroidMediaBrowserServicePlayerEngine(
            application
        )

    @Provides
    @PlayerFrameworkScope
    fun providePlayer(application: Application, logger: Logger): MPOPlayer =
        MPOExoPlayer(
            context = application,
            mainThreadHandler = Handler(Looper.getMainLooper()),
            executor = Executors.newSingleThreadExecutor(),
            exoPlayerBuilder = SimpleExoPlayer.Builder(application.applicationContext),
            logger = logger,
        )

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
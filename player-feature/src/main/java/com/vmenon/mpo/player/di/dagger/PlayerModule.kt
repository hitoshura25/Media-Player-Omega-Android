package com.vmenon.mpo.player.di.dagger

import android.app.Application
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.content.ContextCompat
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.util.MediaHelper
import com.vmenon.mpo.player.usecases.*
import com.vmenon.mpo.player.view.activity.MediaPlayerActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PlayerModule {
    @Provides
    @Singleton
    fun providePlayer(application: Application): MPOPlayer =
        MPOExoPlayer(application)

    @Provides
    fun providePlayerInteractors(playerEngine: MediaPlayerEngine): PlayerInteractors =
        PlayerInteractors(
            ConnectPlayerClient(playerEngine),
            DisconnectPlayerClient(playerEngine),
            ListenForPlaybackStateChanges(playerEngine),
            PlayMedia(playerEngine),
            TogglePlaybackState(playerEngine),
            SkipPlayback(playerEngine),
            SeekToPosition(playerEngine)
        )

    @Provides
    fun provideEpisodeRequestMapper(): PlayerRequestMapper<EpisodeModel> =
        object : PlayerRequestMapper<EpisodeModel> {
            override fun createMediaId(item: EpisodeModel): String = MediaHelper.createMediaId(item)
        }

    @Provides
    fun providePlayerEngine(
        application: Application,
        configuration: MPOMediaBrowserService.Configuration
    ): MediaPlayerEngine = AndroidMediaBrowserServicePlayerEngine(application, configuration)

    @Provides
    fun providesMPOMediaBrowserServiceConfiguration(
        application: Application,
        myLibraryService: MyLibraryService,
        player: MPOPlayer
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        myLibraryService,
        player,
        MediaPlayerActivity::class.java,
        { intent: Intent, mediaSession: MediaSessionCompat ->
            intent.putExtra(
                MediaPlayerActivity.EXTRA_NOTIFICATION_MEDIA_ID,
                mediaSession.controller.metadata?.getString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID
                )
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(application, R.color.colorPrimary)
        })
}
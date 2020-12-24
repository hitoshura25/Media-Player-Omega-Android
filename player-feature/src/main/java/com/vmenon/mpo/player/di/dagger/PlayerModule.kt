package com.vmenon.mpo.player.di.dagger

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.player.MediaPlayerActivityDestination
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
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
            override fun createMediaId(item: EpisodeModel): PlaybackMediaRequest =
                PlaybackMediaRequest(
                    PlaybackMedia(
                        mediaId = "episode:${item.id}",
                        author = item.show.author,
                        album = item.show.name,
                        title = item.name,
                        artworkUrl = item.artworkUrl ?: item.show.artworkUrl,
                        genres = item.show.genres,
                        durationInMillis = item.lengthInSeconds * 1000
                    ),
                    item.filename
                )
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
        { intent: Intent, request: PlaybackMediaRequest? ->
            request?.let {
                intent.putExtra(
                    MediaPlayerActivity.EXTRA_NOTIFICATION_MEDIA_ID,
                    request
                )
            }
        },
        { builder ->
            builder.color = ContextCompat.getColor(application, R.color.colorPrimary)
        })

    @Provides
    fun providePlayerNavigationDestination(): PlayerNavigationDestination =
        MediaPlayerActivityDestination()
}
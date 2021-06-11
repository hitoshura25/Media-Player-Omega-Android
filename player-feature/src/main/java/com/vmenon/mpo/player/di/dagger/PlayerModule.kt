package com.vmenon.mpo.player.di.dagger

import android.app.Application
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.player.usecases.ConnectPlayerClient
import com.vmenon.mpo.player.usecases.DisconnectPlayerClient
import com.vmenon.mpo.player.usecases.ListenForPlaybackStateChanges
import com.vmenon.mpo.player.usecases.PlayMedia
import com.vmenon.mpo.player.usecases.PlayerInteractors
import com.vmenon.mpo.player.usecases.SeekToPosition
import com.vmenon.mpo.player.usecases.SkipPlayback
import com.vmenon.mpo.player.usecases.TogglePlaybackState
import com.vmenon.mpo.player.view.fragment.MediaPlayerFragment
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
    fun providePlayerEngine(application: Application): MediaPlayerEngine =
        AndroidMediaBrowserServicePlayerEngine(application)

    @Provides
    fun providePlayerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
        FragmentDestination(
            fragmentCreator = { MediaPlayerFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = MediaPlayerFragment::class.java.name
        )
}
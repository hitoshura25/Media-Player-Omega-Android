package com.vmenon.mpo.player.di.dagger

import android.app.Application
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.EpisodeModelPlayerRequestMapper
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.framework.exo.MPOExoPlayer
import com.vmenon.mpo.player.usecases.*
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
    fun providePlayerInteractors(
        playerEngine: MediaPlayerEngine,
        system: System
    ): PlayerInteractors =
        PlayerInteractors(
            ConnectPlayerClient(playerEngine),
            DisconnectPlayerClient(playerEngine),
            ListenForPlaybackStateChanges(playerEngine, system),
            PlayMedia(playerEngine),
            TogglePlaybackState(playerEngine),
            SkipPlayback(playerEngine),
            SeekToPosition(playerEngine)
        )

    @Provides
    fun provideEpisodeRequestMapper(): PlayerRequestMapper<EpisodeModel> =
        EpisodeModelPlayerRequestMapper()

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


package com.vmenon.mpo.player.di.dagger

import android.app.Application
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.player.MPOPlayer
import com.vmenon.mpo.player.domain.PlayerEngine
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.player.exo.MPOExoPlayer
import com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.util.MediaHelper
import com.vmenon.mpo.player.usecases.*
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
    fun providePlayerInteractors(playerEngine: PlayerEngine): PlayerInteractors =
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
    fun providePlayerEngine(application: Application): PlayerEngine =
        AndroidMediaBrowserServicePlayerEngine(application, MPOMediaBrowserService::class.java)
}
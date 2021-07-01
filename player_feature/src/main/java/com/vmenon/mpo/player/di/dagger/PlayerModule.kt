package com.vmenon.mpo.player.di.dagger

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.NavigationParamsConverter
import com.vmenon.mpo.player.usecases.*
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.ThreadUtil
import dagger.Module
import dagger.Provides

@Module
object PlayerModule {
    @Provides
    fun providePlayerInteractors(
        playerEngine: MediaPlayerEngine,
        converter: NavigationParamsConverter,
        threadUtil: ThreadUtil,
        clock: Clock
    ): PlayerInteractors =
        PlayerInteractors(
            ConnectPlayerClient(playerEngine),
            DisconnectPlayerClient(playerEngine),
            ListenForPlaybackStateChanges(
                playerEngine,
                clock,
                threadUtil
            ),
            PlayMedia(playerEngine),
            TogglePlaybackState(playerEngine),
            SkipPlayback(playerEngine),
            SeekToPosition(playerEngine),
            HandlePlayerNavigationRequest(converter)
        )

}


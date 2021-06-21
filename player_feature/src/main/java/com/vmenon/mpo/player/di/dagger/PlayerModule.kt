package com.vmenon.mpo.player.di.dagger

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.usecases.*
import dagger.Module
import dagger.Provides

@Module
object PlayerModule {
    @Provides
    fun providePlayerInteractors(
        playerEngine: MediaPlayerEngine,
        system: System
    ): PlayerInteractors =
        PlayerInteractors(
            ConnectPlayerClient(playerEngine),
            DisconnectPlayerClient(playerEngine),
            ListenForPlaybackStateChanges(
                playerEngine,
                system
            ),
            PlayMedia(playerEngine),
            TogglePlaybackState(playerEngine),
            SkipPlayback(playerEngine),
            SeekToPosition(playerEngine)
        )

}


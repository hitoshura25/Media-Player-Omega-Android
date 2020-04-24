package com.vmenon.mpo.player.di

import android.app.Application
import com.vmenon.mpo.player.MPOPlayer
import com.vmenon.mpo.player.exo.MPOExoPlayer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface PlayerModule {
    companion object {
        @Provides
        @Singleton
        fun providePlayer(application: Application): MPOPlayer =
            MPOExoPlayer(application)
    }
}
package com.vmenon.mpo.player.di.dagger

import android.app.Application
import com.vmenon.mpo.player.MPOPlayer
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [PlayerModule::class])
@Singleton
interface PlayerComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): PlayerComponent
    }

    fun player(): MPOPlayer
}
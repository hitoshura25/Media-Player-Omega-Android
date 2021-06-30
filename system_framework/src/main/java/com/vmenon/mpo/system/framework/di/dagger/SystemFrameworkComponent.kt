package com.vmenon.mpo.system.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.system.domain.ThreadUtil
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [SystemFrameworkModule::class])
@Singleton
interface SystemFrameworkComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun build(): SystemFrameworkComponent
    }

    fun logger(): Logger
    fun clock(): Clock
    fun threadUtil(): ThreadUtil
    fun application(): Application
}
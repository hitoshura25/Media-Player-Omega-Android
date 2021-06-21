package com.vmenon.mpo.system.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.common.domain.System
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

    fun system(): System
    fun application(): Application
}
package com.vmenon.mpo.system.framework.di.dagger

import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.system.domain.ThreadUtil
import com.vmenon.mpo.system.framework.ClockImpl
import com.vmenon.mpo.system.framework.LoggerImpl
import com.vmenon.mpo.system.framework.ThreadUtilImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SystemFrameworkModule {
    @Provides
    @Singleton
    fun logger(): Logger = LoggerImpl()

    @Provides
    @Singleton
    fun clock(): Clock = ClockImpl()

    @Provides
    @Singleton
    fun threadUtil(): ThreadUtil = ThreadUtilImpl()
}
package com.vmenon.mpo.system.framework.di.dagger

import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.system.domain.ThreadUtil
import com.vmenon.mpo.system.framework.ClockImpl
import com.vmenon.mpo.system.framework.LoggerImpl
import com.vmenon.mpo.system.framework.ThreadUtilImpl
import dagger.Module
import dagger.Provides

@Module
object SystemFrameworkModule {
    @Provides fun logger(): Logger = LoggerImpl()
    @Provides fun clock(): Clock = ClockImpl()
    @Provides fun threadUtil(): ThreadUtil = ThreadUtilImpl()
}
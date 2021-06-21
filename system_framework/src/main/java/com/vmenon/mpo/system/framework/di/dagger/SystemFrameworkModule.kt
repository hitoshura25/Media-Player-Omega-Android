package com.vmenon.mpo.system.framework.di.dagger

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.system.framework.SystemImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SystemFrameworkModule {
    @Singleton
    @Provides fun provideSystem(): System = SystemImpl()
}
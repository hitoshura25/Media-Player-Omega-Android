package com.vmenon.mpo.auth.test.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsScope
import com.vmenon.mpo.auth.test.MockAndroidBiometricsManager
import com.vmenon.mpo.system.domain.Logger
import dagger.Module
import dagger.Provides

@Module
object TestBiometricsModule {
    @Provides
    @BiometricsScope
    fun provideBiometricsManager(application: Application, logger: Logger): BiometricsManager =
        MockAndroidBiometricsManager(application, logger)
}
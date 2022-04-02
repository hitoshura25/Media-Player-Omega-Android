package com.vmenon.mpo.auth.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.framework.biometrics.AndroidBiometricsManager
import com.vmenon.mpo.system.domain.Logger
import dagger.Module
import dagger.Provides

@Module
object BiometricsModule {
    @Provides
    @BiometricsScope
    fun provideBiometricsManager(application: Application, logger: Logger): BiometricsManager =
        AndroidBiometricsManager(application, logger)
}
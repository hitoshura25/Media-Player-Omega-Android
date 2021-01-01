package com.mpo.core.di

import com.mpo.core.ReleaseThirdPartyIntegrator
import com.vmenon.mpo.core.ThirdPartyIntegrator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ThirdPartyIntegratorModule {
    @Provides
    @Singleton
    fun thirdPartyIntegrator(): ThirdPartyIntegrator = ReleaseThirdPartyIntegrator()
}
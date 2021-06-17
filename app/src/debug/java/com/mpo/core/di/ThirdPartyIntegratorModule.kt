package com.mpo.core.di

import com.mpo.core.DebugThirdPartyIntegrator
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.di.AppScope
import dagger.Module
import dagger.Provides

@Module
class ThirdPartyIntegratorModule {
    @Provides
    @AppScope
    fun thirdPartyIntegrator(): ThirdPartyIntegrator = DebugThirdPartyIntegrator()
}
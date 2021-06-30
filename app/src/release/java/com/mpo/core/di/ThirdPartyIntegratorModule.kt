package com.mpo.core.di

import com.mpo.core.ReleaseThirdPartyIntegrator
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.di.AppScope
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ThirdPartyIntegratorModule {
    @Provides
    @AppScope
    fun thirdPartyIntegrator(): ThirdPartyIntegrator = ReleaseThirdPartyIntegrator()
}
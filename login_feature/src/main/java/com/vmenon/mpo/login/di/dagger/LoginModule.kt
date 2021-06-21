package com.vmenon.mpo.login.di.dagger

import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import dagger.Module
import dagger.Provides

@Module
object LoginModule {
    @Provides
    fun provideAuthService(commonFrameworkComponent: CommonFrameworkComponent): AuthService =
        commonFrameworkComponent.authComponent().authService()
}
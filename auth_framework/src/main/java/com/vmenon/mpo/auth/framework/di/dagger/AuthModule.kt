package com.vmenon.mpo.auth.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.framework.AuthServiceImpl
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.auth.framework.openid.OpenIdAuthenticator
import com.vmenon.mpo.system.domain.Logger
import dagger.Module
import dagger.Provides

@Module
object AuthModule {
    @Provides
    @AuthScope
    fun provideAuthService(
        authState: AuthState,
        authenticator: Authenticator,
        biometricsManager: BiometricsManager
    ): AuthService =
        AuthServiceImpl(authState, authenticator, biometricsManager)

    // TODO: Probably makes more sense to move this into OpenIdAuthModule and have AuthComponent
    // depend on OpenIdAuthComponent
    @Provides
    @AuthScope
    fun provideAuthenticator(
        application: Application,
        authState: AuthState,
        logger: Logger
    ): Authenticator =
        OpenIdAuthenticator(application, authState, logger)
}
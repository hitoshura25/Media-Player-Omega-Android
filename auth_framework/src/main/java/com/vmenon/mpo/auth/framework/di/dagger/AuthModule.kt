package com.vmenon.mpo.auth.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.framework.AuthServiceImpl
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.auth.framework.SharedPrefsAuthState
import com.vmenon.mpo.auth.framework.openid.OpenIdAuthenticator
import com.vmenon.mpo.common.domain.System
import dagger.Module
import dagger.Provides

@Module
object AuthModule {
    @Provides
    @AuthScope
    fun provideAuthService(
        authState: AuthState,
        authenticator: Authenticator,
    ): AuthService =
        AuthServiceImpl(authState, authenticator)

    @Provides
    @AuthScope
    fun provideAuthState(application: Application): AuthState = SharedPrefsAuthState(application)

    @Provides
    @AuthScope
    fun provideAuthenticator(
        application: Application,
        authState: AuthState,
        system: System
    ): Authenticator =
        OpenIdAuthenticator(application, authState, system)

}
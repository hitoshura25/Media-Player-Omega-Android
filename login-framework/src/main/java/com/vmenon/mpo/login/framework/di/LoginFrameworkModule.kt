package com.vmenon.mpo.login.framework.di

import android.app.Application
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.framework.openid.OpenIdAuthenticator
import dagger.Module
import dagger.Provides

@Module
class LoginFrameworkModule {
    @Provides
    fun provideAuthenticator(
        application: Application,
        authState: AuthState,
        system: System
    ): Authenticator = OpenIdAuthenticator(application, authState, system)
}
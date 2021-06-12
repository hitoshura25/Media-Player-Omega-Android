package com.vmenon.mpo.login.framework.di

import android.app.Application
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.data.UserCache
import com.vmenon.mpo.login.framework.SharedPrefsUserCache
import com.vmenon.mpo.login.framework.openid.OpenIdAuthenticator
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class LoginFrameworkModule {
    @Provides
    fun provideAuthenticator(
        application: Application,
        authState: AuthState,
        system: System
    ): Authenticator = OpenIdAuthenticator(application, authState, system)

    @Provides
    @Singleton
    fun provideUserCache(application: Application, system: System): UserCache =
        SharedPrefsUserCache(application, system, TimeUnit.MINUTES.toMillis(15))

}
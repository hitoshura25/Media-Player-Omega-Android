package com.vmenon.mpo.login.di

import android.app.Application
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.data.AuthRepository
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.data.LoginRepository
import com.vmenon.mpo.login.data.UserRegistry
import com.vmenon.mpo.login.domain.AuthService
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.framework.MpoApiUserRegistry
import com.vmenon.mpo.login.framework.SharedPrefsAuthState
import com.vmenon.mpo.login.view.LoginFragment
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.view.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoginModule {
    @Provides
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        FragmentDestination(
            fragmentCreator = { LoginFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = LoginFragment::class.java.name
        )

    @Provides
    fun provideLoginService(
        userRegistry: UserRegistry,
        authState: AuthState,
        system: System
    ): LoginService = LoginRepository(userRegistry, authState, system)

    @Provides
    fun provideAuthService(
        authState: AuthState,
        authenticator: Authenticator
    ): AuthService = AuthRepository(authState, authenticator)

    @Provides
    fun provideUserRegistry(
        api: MediaPlayerOmegaRetrofitService
    ): UserRegistry = MpoApiUserRegistry(api)

    @Provides
    @Singleton
    fun provideAuthState(application: Application): AuthState = SharedPrefsAuthState(application)
}
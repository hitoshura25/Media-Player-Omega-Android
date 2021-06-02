package com.vmenon.mpo.login.di

import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.login.view.LoginFragment
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.view.R
import dagger.Module
import dagger.Provides

@Module
class LoginModule {
    @Provides
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        FragmentDestination(
            fragmentCreator = { LoginFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = LoginFragment::class.java.name
        )
}
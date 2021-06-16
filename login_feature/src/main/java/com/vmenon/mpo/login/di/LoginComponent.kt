package com.vmenon.mpo.login.di

import com.vmenon.mpo.login.view.LoginFragment
import com.vmenon.mpo.login.viewmodel.LoginViewModel
import dagger.Subcomponent

@Subcomponent
@LoginScope
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(viewModel: LoginViewModel)
}
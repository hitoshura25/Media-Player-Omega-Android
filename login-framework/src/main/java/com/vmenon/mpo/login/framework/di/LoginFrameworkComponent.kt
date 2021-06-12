package com.vmenon.mpo.login.framework.di

import com.vmenon.mpo.login.framework.openid.fragment.OpenIdHandlerFragment
import com.vmenon.mpo.login.framework.openid.viewmodel.OpenIdHandlerViewModel
import dagger.Subcomponent

@Subcomponent
@LoginFrameworkScope
interface LoginFrameworkComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginFrameworkComponent
    }

    fun inject(viewModel: OpenIdHandlerViewModel)
    fun inject(fragment: OpenIdHandlerFragment)
}
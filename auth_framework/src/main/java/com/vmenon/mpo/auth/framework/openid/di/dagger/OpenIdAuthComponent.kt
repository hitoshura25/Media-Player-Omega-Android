package com.vmenon.mpo.auth.framework.openid.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment
import com.vmenon.mpo.auth.framework.openid.viewmodel.OpenIdHandlerViewModel
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(dependencies = [AuthComponent::class, SystemFrameworkComponent::class])
@OpenIdAuthScope
interface OpenIdAuthComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun authComponent(component: AuthComponent): Builder
        fun build(): OpenIdAuthComponent
    }

    fun inject(viewModel: OpenIdHandlerViewModel)
    fun inject(fragment: OpenIdHandlerFragment)
}
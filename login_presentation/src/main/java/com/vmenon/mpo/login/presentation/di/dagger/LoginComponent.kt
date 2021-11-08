package com.vmenon.mpo.login.presentation.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponent
import com.vmenon.mpo.login.presentation.fragment.LoginFragment
import com.vmenon.mpo.login.presentation.viewmodel.LoginViewModel
import dagger.Component

@Component(
    dependencies = [LoginFrameworkComponent::class, CommonFrameworkComponent::class],
    modules = [LoginModule::class]
)
@LoginScope
interface LoginComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun loginFrameworkComponent(frameworkComponent: LoginFrameworkComponent): Builder
        fun build(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(viewModel: LoginViewModel)
}
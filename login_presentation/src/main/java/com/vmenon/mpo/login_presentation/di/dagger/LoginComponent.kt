package com.vmenon.mpo.login_presentation.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponent
import com.vmenon.mpo.login_feature.view.LoginFragment
import com.vmenon.mpo.login_presentation.viewmodel.LoginViewModel
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
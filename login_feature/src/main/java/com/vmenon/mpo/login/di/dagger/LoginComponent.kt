package com.vmenon.mpo.login.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponent
import com.vmenon.mpo.login.view.LoginFragment
import com.vmenon.mpo.login.viewmodel.LoginViewModel
import dagger.Component

@Component(
    dependencies = [AppComponent::class, LoginFrameworkComponent::class, CommonFrameworkComponent::class],
    modules = [LoginModule::class]
)
@LoginScope
interface LoginComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun loginFrameworkComponent(frameworkComponent: LoginFrameworkComponent): Builder
        fun appComponent(component: AppComponent): Builder
        fun build(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(viewModel: LoginViewModel)
}
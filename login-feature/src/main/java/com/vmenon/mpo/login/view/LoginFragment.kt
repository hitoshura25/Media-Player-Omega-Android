package com.vmenon.mpo.login.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmenon.mpo.login.databinding.FragmentLoginBinding
import com.vmenon.mpo.login.di.LoginComponent
import com.vmenon.mpo.login.di.LoginComponentProvider
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.login.viewmodel.LoginViewModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment

class LoginFragment : BaseViewBindingFragment<LoginComponent, FragmentLoginBinding>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(LoginNavigationLocation) {

    private val viewModel: LoginViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun setupComponent(context: Context): LoginComponent =
        (context as LoginComponentProvider).loginComponent()

    override fun inject(component: LoginComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

}
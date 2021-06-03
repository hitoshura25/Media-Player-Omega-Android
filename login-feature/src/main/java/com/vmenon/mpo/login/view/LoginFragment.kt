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
import com.vmenon.mpo.login.model.LoggedInState
import com.vmenon.mpo.login.model.LoginState
import com.vmenon.mpo.login.model.RegisterState
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loginState().observe(viewLifecycleOwner, { state ->
            when (state) {
                LoginState -> {
                    binding.loginForm.root.visibility = View.VISIBLE
                    binding.registerForm.root.visibility = View.GONE
                    binding.accountView.root.visibility = View.GONE
                }
                RegisterState -> {
                    binding.registerForm.root.visibility = View.VISIBLE
                    binding.loginForm.root.visibility = View.GONE
                    binding.accountView.root.visibility = View.GONE
                }
                is LoggedInState -> {
                    binding.accountView.root.visibility = View.VISIBLE
                    binding.loginForm.root.visibility = View.GONE
                    binding.registerForm.root.visibility = View.GONE
                }
            }
        })

        binding.loginForm.registerLink.setOnClickListener {
            viewModel.registerClicked()
        }
    }
}
package com.vmenon.mpo.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmenon.mpo.login.R
import com.vmenon.mpo.login.databinding.FragmentLoginBinding
import com.vmenon.mpo.login.di.LoginComponent
import com.vmenon.mpo.login.di.LoginComponentProvider
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.login.model.LoadingState
import com.vmenon.mpo.login.model.LoggedInState
import com.vmenon.mpo.login.model.LoginState
import com.vmenon.mpo.login.model.RegisterState
import com.vmenon.mpo.login.viewmodel.LoginViewModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment
import com.vmenon.mpo.view.LoadingStateHelper
import com.vmenon.mpo.view.activity.BaseActivity

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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("OnActivityResult")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (requireActivity() as BaseActivity<*>)
        val loadingStateHelper = LoadingStateHelper.overlayContent(activity.requireLoadingView())
        activity.setSupportActionBar(binding.toolbar)
        viewModel.loginState().observe(viewLifecycleOwner, { event ->
            event.unhandledContent()?.let { state ->
                when (state) {
                    LoginState -> {
                        loadingStateHelper.showContentState()
                        binding.loginForm.root.visibility = View.VISIBLE
                        binding.registerForm.root.visibility = View.GONE
                        binding.accountView.root.visibility = View.GONE
                        activity.title = ""
                    }
                    RegisterState -> {
                        loadingStateHelper.showContentState()
                        binding.registerForm.root.visibility = View.VISIBLE
                        binding.loginForm.root.visibility = View.GONE
                        binding.accountView.root.visibility = View.GONE
                        activity.title = ""
                    }

                    is LoggedInState -> {
                        loadingStateHelper.showContentState()
                        binding.accountView.root.visibility = View.VISIBLE
                        binding.loginForm.root.visibility = View.GONE
                        binding.registerForm.root.visibility = View.GONE
                        activity.title = getString(R.string.hi_user, state.userDetails.firstName)
                    }
                    LoadingState -> {
                        loadingStateHelper.showLoadingState()
                    }
                }
            }
        })

        binding.loginForm.registerLink.setOnClickListener {
            viewModel.registerClicked()
        }
        binding.loginForm.loginLink.setOnClickListener {
            viewModel.loginClicked(requireActivity())
        }

        binding.registerForm.registerUser.setOnClickListener {
            viewModel.performRegistration(
                binding.registerForm.firstName.text.toString(),
                binding.registerForm.lastName.text.toString(),
                binding.registerForm.email.text.toString(),
                binding.registerForm.password.text.toString(),
                binding.registerForm.confirmPassword.text.toString(),
                requireActivity()
            )
        }

        binding.accountView.logoutLink.setOnClickListener {
            viewModel.logoutClicked(requireActivity())
        }
    }
}
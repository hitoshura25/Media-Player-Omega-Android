package com.vmenon.mpo.login_feature.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmenon.mpo.common.framework.livedata.observeUnhandled
import com.vmenon.mpo.login_feature.databinding.FragmentLoginBinding
import com.vmenon.mpo.login_feature.di.dagger.LoginComponent
import com.vmenon.mpo.login_feature.di.dagger.toLoginComponent
import com.vmenon.mpo.navigation.domain.login.LoginNavigationLocation
import com.vmenon.mpo.login_feature.model.LoadingState
import com.vmenon.mpo.login_feature.viewmodel.LoginViewModel
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

    override fun setupComponent(context: Context): LoginComponent = context.toLoginComponent()

    override fun inject(component: LoginComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (requireActivity() as BaseActivity<*>)
        val loadingStateHelper = LoadingStateHelper.overlayContent(activity.requireLoadingView())
        navigationController.setupWith(
            this,
            binding.toolbar,
            drawerLayout(),
            navigationView()
        )
        viewModel.loginState().observeUnhandled(viewLifecycleOwner, { state ->
            when (state) {
                LoadingState -> {
                    loadingStateHelper.showLoadingState()
                }
                else -> {
                    loadingStateHelper.showContentState()
                }
            }
            binding.state = state
        })

        binding.lifecycleOwner = viewLifecycleOwner
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

    override fun onResume() {
        super.onResume()
        viewModel.fetchState()
    }
}
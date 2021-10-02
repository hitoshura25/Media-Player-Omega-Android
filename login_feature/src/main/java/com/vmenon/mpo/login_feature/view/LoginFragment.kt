package com.vmenon.mpo.login_feature.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.vmenon.mpo.common.framework.livedata.observeUnhandled
import com.vmenon.mpo.login_feature.databinding.FragmentLoginBinding
import com.vmenon.mpo.login_feature.di.dagger.LoginComponent
import com.vmenon.mpo.login_feature.di.dagger.toLoginComponent
import com.vmenon.mpo.navigation.domain.login.LoginNavigationLocation
import com.vmenon.mpo.login_feature.model.LoadingState
import com.vmenon.mpo.login_feature.model.LoggedInState
import com.vmenon.mpo.login_feature.viewmodel.LoginViewModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment
import com.vmenon.mpo.view.LoadingStateHelper
import com.vmenon.mpo.view.R
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
                is LoggedInState -> {
                    loadingStateHelper.showContentState()
                    if (state.promptToEnrollInBiometrics) {
                        promptToSetupBiometrics()
                    }
                }
                else -> {
                    loadingStateHelper.showContentState()
                }
            }
            binding.state = state
        })

        binding.registration = viewModel.registration
        binding.registrationValid = viewModel.registrationValid()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginForm.registerLink.setOnClickListener {
            viewModel.registerClicked()
        }
        binding.loginForm.loginLink.setOnClickListener {
            viewModel.loginClicked(this)
        }
        binding.loginForm.useBiometrics.setOnClickListener {
            viewModel.loginWithBiometrics(this)
        }
        binding.registerForm.registerUser.setOnClickListener {
            viewModel.performRegistration(this)
        }
        binding.accountView.logoutLink.setOnClickListener {
            viewModel.logoutClicked(this)
        }
    }

    private fun promptToSetupBiometrics() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.userWantsToEnrollInBiometrics(this@LoginFragment)
            }
            setNegativeButton(R.string.no) { _, _ ->
                viewModel.userDoesNotWantBiometrics()
            }
            setTitle(getString(com.vmenon.mpo.login_feature.R.string.use_biometrics))
            setMessage(getString(com.vmenon.mpo.login_feature.R.string.use_biometrics_for_login))
        }
        builder.create().show()
    }
}
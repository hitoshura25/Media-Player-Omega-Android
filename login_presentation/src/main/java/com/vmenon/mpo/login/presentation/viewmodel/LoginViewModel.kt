package com.vmenon.mpo.login.presentation.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.login.presentation.R
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.presentation.RegistrationFormValidator
import com.vmenon.mpo.login.presentation.model.AccountState
import com.vmenon.mpo.login.presentation.model.LoadingState
import com.vmenon.mpo.login.presentation.model.LoggedInState
import com.vmenon.mpo.login.presentation.model.LoginState
import com.vmenon.mpo.login.presentation.model.RegisterState
import com.vmenon.mpo.login.presentation.model.RegistrationObservable
import com.vmenon.mpo.login.presentation.model.RegistrationValid
import com.vmenon.mpo.system.domain.BuildConfigProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var loginService: LoginService

    @Inject
    lateinit var biometricsManager: BiometricsManager

    @Inject
    lateinit var buildConfigProvider: BuildConfigProvider

    internal var dispatcher: CoroutineDispatcher = Dispatchers.IO

    val registration by lazy {
        val registrationObservable = RegistrationObservable()
        registrationObservable.addOnPropertyChangedCallback(validator)
        registrationObservable
    }

    private val validator = RegistrationFormValidator()
    private val loginStateFromUI = MutableLiveData<ContentEvent<AccountState>>()

    private val loginState by lazy {
        MediatorLiveData<ContentEvent<AccountState>>().apply {
            addSource(authService.credentials().asLiveData()) { credentialsResult ->
                when (credentialsResult) {
                    CredentialsResult.None,
                    is CredentialsResult.RequiresBiometricAuth -> postState(false, this)
                    is CredentialsResult.Success -> postState(true, this)
                }
            }
            addSource(loginStateFromUI) { value ->
                postValue(value)
            }
        }
    }

    fun registrationValid(): LiveData<RegistrationValid> = validator.registrationValid()

    fun loginState(): LiveData<ContentEvent<AccountState>> = loginState

    fun registerClicked() {
        loginStateFromUI.postValue(RegisterState.toContentEvent())
    }

    fun loginClicked(fragment: Fragment) {
        viewModelScope.launch(dispatcher) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.startAuthentication(fragment.requireActivity())
        }
    }

    fun logoutClicked(fragment: Fragment) {
        viewModelScope.launch(dispatcher) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.logout(fragment.requireActivity())
            loginService.userLoggedOut()
        }
    }

    fun performRegistration(fragment: Fragment) {
        viewModelScope.launch(dispatcher) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            loginService.registerUser(
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getPassword()
            )
            authService.startAuthentication(fragment.requireActivity())
        }
    }

    fun userDoesNotWantBiometrics() {
        viewModelScope.launch(dispatcher) {
            loginService.userDeclinedBiometricsEnrollment()
        }
    }

    fun userWantsToEnrollInBiometrics(fragment: Fragment) {
        viewModelScope.launch {
            loginService.askedToEnrollInBiometrics()
        }
        biometricsManager.requestBiometricPrompt(
            fragment,
            PromptRequest(
                reason = PromptReason.Encryption,
                title = fragment.getString(R.string.enroll_in_biometrics),
                subtitle = fragment.getString(R.string.confirm_to_complete_enrollment),
                confirmationRequired = false,
                negativeActionText = fragment.getString(R.string.cancel)
            )
        )
    }

    fun loginWithBiometrics(fragment: Fragment) {
        viewModelScope.launch {
            val credentialsResult = authService.getCredentials()
            if (credentialsResult is CredentialsResult.RequiresBiometricAuth) {
                biometricsManager.requestBiometricPrompt(
                    fragment,
                    PromptRequest(
                        reason = PromptReason.Decryption(credentialsResult.encryptedData),
                        title = fragment.getString(R.string.login),
                        subtitle = fragment.getString(R.string.confirm_to_login),
                        confirmationRequired = false,
                        negativeActionText = fragment.getString(R.string.cancel)
                    )
                )
            }
        }
    }

    private fun postState(
        authenticated: Boolean,
        mutableLiveData: MutableLiveData<ContentEvent<AccountState>>
    ) {
        viewModelScope.launch(dispatcher) {
            mutableLiveData.postValue(LoadingState.toContentEvent())
            if (authenticated) {
                val userResult = loginService.getUser()
                userResult.onSuccess { user ->
                    mutableLiveData.postValue(
                        LoggedInState(
                            user,
                            shouldPromptToEnrollInBiometrics(),
                            buildConfigProvider.appVersion(),
                            buildConfigProvider.buildNumber()
                        ).toContentEvent()
                    )
                }
                userResult.onFailure {
                    postLoginState(mutableLiveData)
                }
            } else {
                postLoginState(mutableLiveData)
            }
        }
    }

    private suspend fun postLoginState(mutableLiveData: MutableLiveData<ContentEvent<AccountState>>) {
        mutableLiveData.postValue(
            LoginState(
                canUseBiometrics(),
                buildConfigProvider.appVersion(),
                buildConfigProvider.buildNumber()
            ).toContentEvent()
        )
    }

    private suspend fun canUseBiometrics() =
        loginService.isEnrolledInBiometrics() && biometricsManager.deviceSupportsBiometrics()

    private suspend fun shouldPromptToEnrollInBiometrics() =
        !loginService.hasAskedToEnrollInBiometrics() &&
                !loginService.didUserDeclineBiometricsEnrollment() &&
                !loginService.isEnrolledInBiometrics() &&
                biometricsManager.deviceSupportsBiometrics()
}
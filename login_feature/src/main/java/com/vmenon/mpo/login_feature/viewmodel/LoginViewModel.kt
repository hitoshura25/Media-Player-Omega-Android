package com.vmenon.mpo.login_feature.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.vmenon.mpo.R
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason.*
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login_feature.RegistrationFormValidator
import com.vmenon.mpo.login_feature.model.*
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
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.startAuthentication(fragment.requireActivity())
        }
    }

    fun logoutClicked(fragment: Fragment) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.logout(fragment.requireActivity())
        }
    }

    fun performRegistration(fragment: Fragment) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            loginService.userDeclinedBiometricsEnrollment()
        }
    }

    fun userWantsToEnrollInBiometrics(fragment: Fragment) {
        biometricsManager.requestBiometricPrompt(
            fragment,
            PromptRequest(
                reason = Encryption,
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
                        reason = Decryption(credentialsResult.encryptedData),
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
        viewModelScope.launch(Dispatchers.IO) {
            mutableLiveData.postValue(LoadingState.toContentEvent())
            if (authenticated) {
                loginService.getUser().onSuccess { user ->
                    mutableLiveData.postValue(
                        LoggedInState(
                            user,
                            shouldPromptToEnrollInBiometrics()
                        ).toContentEvent()
                    )
                }
            } else {
                mutableLiveData.postValue(
                    LoginState(canUseBiometrics()).toContentEvent()
                )
            }
        }
    }

    private suspend fun canUseBiometrics() =
        loginService.isEnrolledInBiometrics() && biometricsManager.deviceSupportsBiometrics()

    private suspend fun shouldPromptToEnrollInBiometrics() =
        !loginService.didUserDeclineBiometricsEnrollment() &&
                !loginService.isEnrolledInBiometrics() &&
                biometricsManager.deviceSupportsBiometrics()
}
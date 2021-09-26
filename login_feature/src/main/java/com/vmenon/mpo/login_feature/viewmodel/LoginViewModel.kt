package com.vmenon.mpo.login_feature.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricState
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
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

    private var biometricEnrollmentContract: ActivityResultLauncher<Intent>? = null

    val registration by lazy {
        val registrationObservable = RegistrationObservable()
        registrationObservable.addOnPropertyChangedCallback(validator)
        registrationObservable
    }

    private val validator = RegistrationFormValidator()
    private val loginStateFromUI = MutableLiveData<ContentEvent<AccountState>>()
    private val biometricStateChanged = MutableLiveData<Unit>()

    private val loginState by lazy {
        MediatorLiveData<ContentEvent<AccountState>>().apply {
            addSource(authService.authenticated().asLiveData()) { authenticated ->
                postState(authenticated, this)
            }
            addSource(loginStateFromUI) { value ->
                postValue(value)
            }
            addSource(biometricStateChanged) {
                viewModelScope.launch {
                    postState(authService.isAuthenticated(), this@apply)
                }
            }
        }
    }

    fun onCreate(fragment: Fragment) {
        biometricEnrollmentContract = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                biometricStateChanged.postValue(Unit)
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
        viewModelScope.launch {
            biometricsManager.requestBiometricPrompt(PromptReason.ENROLLMENT)
        }
    }

    fun loginWithBiometrics() {
        viewModelScope.launch {
            biometricsManager.requestBiometricPrompt(PromptReason.LOGIN)
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

    private suspend fun canUseBiometrics() = if (loginService.isEnrolledInBiometrics()) {
        when (biometricsManager.biometricState()) {
            BiometricState.SUCCESS -> true
            else -> false
        }
    } else false

    private suspend fun shouldPromptToEnrollInBiometrics() =
        if (!loginService.didUserDeclineBiometricsEnrollment() &&
            !loginService.isEnrolledInBiometrics()
        ) {
            when (biometricsManager.biometricState()) {
                BiometricState.SUCCESS,
                BiometricState.REQUIRES_ENROLLMENT -> true
                else -> false
            }
        } else false
}
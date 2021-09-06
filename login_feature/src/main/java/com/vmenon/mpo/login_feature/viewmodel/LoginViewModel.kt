package com.vmenon.mpo.login_feature.viewmodel

import android.app.Activity
import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
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
    private val refreshState = MutableLiveData<Unit>()

    private val loginState by lazy {
        MediatorLiveData<ContentEvent<AccountState>>().apply {
            addSource(authService.authenticated().asLiveData()) { authenticated ->
                postState(authenticated, this)
            }
            addSource(loginStateFromUI) { value ->
                postValue(value)
            }
            addSource(refreshState) {
                postState(authService.isAuthenticated(), this)
            }
        }
    }

    fun registrationValid(): LiveData<RegistrationValid> = validator.registrationValid()

    fun fetchState() {
        refreshState.postValue(Unit)
    }

    fun loginState(): LiveData<ContentEvent<AccountState>> = loginState

    fun registerClicked() {
        loginStateFromUI.postValue(RegisterState.toContentEvent())
    }

    fun loginClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.startAuthentication(activity)
        }
    }

    fun logoutClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.logout(activity)
        }
    }

    fun performRegistration(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            loginService.registerUser(
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getPassword()
            )
            authService.startAuthentication(activity)
        }
    }

    private fun postState(
        authenticated: Boolean,
        mutableLiveData: MutableLiveData<ContentEvent<AccountState>>
    ) {
        if (authenticated) {
            viewModelScope.launch(Dispatchers.IO) {
                mutableLiveData.postValue(LoadingState.toContentEvent())
                loginService.getUser().onSuccess { user ->
                    mutableLiveData.postValue(LoggedInState(user).toContentEvent())
                }
            }
        } else {
            mutableLiveData.postValue(LoginState(biometricsManager.biometricState()).toContentEvent())
        }
    }
}
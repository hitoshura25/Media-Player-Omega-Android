package com.vmenon.mpo.login.viewmodel

import android.app.Activity
import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.model.AccountState
import com.vmenon.mpo.login.model.LoadingState
import com.vmenon.mpo.login.model.LoggedInState
import com.vmenon.mpo.login.model.LoginState
import com.vmenon.mpo.login.model.RegisterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var loginService: LoginService

    private val loginStateFromUI = MutableLiveData<ContentEvent<AccountState>>()
    private val refreshState = MutableLiveData<Unit>()

    fun fetchState() {
        refreshState.postValue(Unit)
    }

    fun loginState(): LiveData<ContentEvent<AccountState>> =
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

    fun performRegistration(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        activity: Activity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            loginService.registerUser(firstName, lastName, email, password)
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
            mutableLiveData.postValue(LoginState.toContentEvent())
        }
    }
}
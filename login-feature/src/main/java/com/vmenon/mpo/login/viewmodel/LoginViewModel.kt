package com.vmenon.mpo.login.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.login.domain.AuthService
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

    private val loginStateFromUI = MutableLiveData<AccountState>()

    fun loginState(): LiveData<AccountState> = MediatorLiveData<AccountState>().apply {
        addSource(authService.authenticated().asLiveData()) { authenticated ->
            if (authenticated) {
                viewModelScope.launch(Dispatchers.IO) {
                    postValue(LoadingState)
                    postValue(LoggedInState(loginService.getUser()))
                }
            } else {
                postValue(LoginState)
            }
        }
        addSource(loginStateFromUI) { value ->
            postValue(value)
        }
    }

    fun registerClicked() {
        loginStateFromUI.postValue(RegisterState)
    }

    fun loginClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState)
            authService.startAuthentication(activity)
        }
    }

    fun logoutClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState)
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
            loginStateFromUI.postValue(LoadingState)
            loginService.registerUser(firstName, lastName, email, password)
            authService.startAuthentication(activity)
        }
    }
}
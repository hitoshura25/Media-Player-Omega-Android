package com.vmenon.mpo.login.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val loginState = MutableLiveData<AccountState>()

    fun loginState(): LiveData<AccountState> = loginState

    fun fetchLoginState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (authService.isAuthenticated()) {
                loginState.postValue(LoadingState)
                loginState.postValue(LoggedInState(loginService.getUser()))
            } else {
                loginState.postValue(LoginState)
            }
        }
    }

    fun registerClicked() {
        loginState.postValue(RegisterState)
    }

    fun loginClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginState.postValue(LoadingState)
            authService.startAuthentication(activity)
        }
    }

    fun logoutClicked(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            loginState.postValue(LoadingState)
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
            loginState.postValue(LoadingState)
            loginService.registerUser(firstName, lastName, email, password)
            authService.startAuthentication(activity)
        }
    }
}
package com.vmenon.mpo.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vmenon.mpo.login.model.AccountState
import com.vmenon.mpo.login.model.LoginState
import com.vmenon.mpo.login.model.RegisterState

class LoginViewModel : ViewModel() {
    private val loginState: MutableLiveData<AccountState> by lazy {
        println("Getting login state")
        MutableLiveData(LoginState)
    }

    fun loginState(): LiveData<AccountState> = loginState

    fun registerClicked() {
        loginState.postValue(RegisterState)
    }
}
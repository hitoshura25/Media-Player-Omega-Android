package com.vmenon.mpo.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vmenon.mpo.login.model.AccountState
import com.vmenon.mpo.login.model.LoginState

class LoginViewModel : ViewModel() {
    val loginState: LiveData<AccountState> by lazy {
        println("Getting login state")
        MutableLiveData(LoginState)
    }
}
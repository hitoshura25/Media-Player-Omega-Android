package com.vmenon.mpo.login.model

sealed class AccountState
object LoginState : AccountState()
object RegisterState : AccountState()
data class LoggedInState(val username: String, val firstName: String, val lastName: String) :
    AccountState()
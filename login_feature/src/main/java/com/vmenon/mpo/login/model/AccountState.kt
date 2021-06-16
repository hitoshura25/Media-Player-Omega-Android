package com.vmenon.mpo.login.model

import com.vmenon.mpo.login.domain.User

sealed class AccountState
object LoginState : AccountState()
object RegisterState : AccountState()
object LoadingState : AccountState()
data class LoggedInState(val userDetails: User) : AccountState()
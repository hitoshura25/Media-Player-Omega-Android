package com.vmenon.mpo.login_feature.model

import com.vmenon.mpo.login.domain.User

sealed class AccountState
data class LoginState(val canUseBiometrics: Boolean) : AccountState()
object RegisterState : AccountState()
object LoadingState : AccountState()
data class LoggedInState(val userDetails: User, val promptToEnrollInBiometrics: Boolean) :
    AccountState()
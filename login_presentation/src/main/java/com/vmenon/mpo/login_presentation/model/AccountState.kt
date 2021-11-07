package com.vmenon.mpo.login_presentation.model

import com.vmenon.mpo.login.domain.User

sealed class AccountState
data class LoginState(
    val canUseBiometrics: Boolean,
    val version: String,
    val buildNumber: String
) : AccountState()

object RegisterState : AccountState()
object LoadingState : AccountState()
data class LoggedInState(
    val userDetails: User,
    val promptToEnrollInBiometrics: Boolean,
    val version: String,
    val buildNumber: String
) : AccountState()
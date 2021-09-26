package com.vmenon.mpo.auth.domain

sealed class CredentialsResult {
    object RequiresBiometricAuth : CredentialsResult()
    object None : CredentialsResult()
    data class Success(val credentials: Credentials) : CredentialsResult()
}
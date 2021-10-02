package com.vmenon.mpo.auth.domain

sealed class CredentialsResult {
    data class RequiresBiometricAuth(val encryptedData: CipherEncryptedData) :
        CredentialsResult()

    data class Success(val credentials: Credentials) : CredentialsResult()

    object None : CredentialsResult()
}
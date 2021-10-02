package com.vmenon.mpo.auth.data

import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher

interface AuthState {
    fun credentials(): Flow<CredentialsResult>
    suspend fun isLoggedOut(): Boolean
    suspend fun getCredentials(): CredentialsResult
    suspend fun storeCredentials(credentials: Credentials)
    suspend fun clearCredentials()
    suspend fun encryptCredentials(cipher: Cipher)
    suspend fun decryptCredentials(cipher: Cipher)
}
package com.vmenon.mpo.auth.data

import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher

interface AuthState {
    fun credentials(): Flow<CredentialsResult>
    suspend fun didUserLogOut(): Boolean
    suspend fun getCredentials(): CredentialsResult
    suspend fun storeCredentials(credentials: Credentials)
    suspend fun userLoggedOut()
    suspend fun encryptCredentials(cipher: Cipher)
    suspend fun decryptCredentials(cipher: Cipher)
}
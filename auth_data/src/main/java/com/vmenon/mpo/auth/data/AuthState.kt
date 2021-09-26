package com.vmenon.mpo.auth.data

import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import kotlinx.coroutines.flow.Flow

interface AuthState {
    suspend fun getCredentials(): CredentialsResult
    fun credentials(): Flow<CredentialsResult>

    suspend fun storeCredentials(credentials: Credentials)
    suspend fun clearCredentials()
}
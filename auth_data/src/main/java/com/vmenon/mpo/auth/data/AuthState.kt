package com.vmenon.mpo.auth.data

import com.vmenon.mpo.auth.domain.Credentials
import kotlinx.coroutines.flow.Flow

interface AuthState {
    fun getCredentials(): Credentials?
    fun credentials(): Flow<Credentials?>

    suspend fun storeCredentials(credentials: Credentials)
    suspend fun clearCredentials()
}
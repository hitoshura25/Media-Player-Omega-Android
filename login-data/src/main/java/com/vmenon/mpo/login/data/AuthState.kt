package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.Credentials
import kotlinx.coroutines.flow.Flow

interface AuthState {
    fun getCredentials(): Credentials?
    fun credentials(): Flow<Credentials?>

    suspend fun storeCredentials(credentials: Credentials)
    suspend fun clearCredentials()
}
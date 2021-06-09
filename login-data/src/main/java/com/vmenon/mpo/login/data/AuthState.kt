package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.Credentials

interface AuthState {
    fun getCredentials(): Credentials?
    suspend fun storeCredentials(credentials: Credentials)
    suspend fun clearCredentials()
}
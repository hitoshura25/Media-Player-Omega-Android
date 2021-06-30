package com.vmenon.mpo.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthService {
    fun getCredentials(): Credentials?
    fun isAuthenticated(): Boolean = getCredentials() != null
    fun authenticated(): Flow<Boolean>
    suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: (Boolean) -> T
    ): T

    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
}
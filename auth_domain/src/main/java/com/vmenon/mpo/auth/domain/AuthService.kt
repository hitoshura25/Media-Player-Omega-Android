package com.vmenon.mpo.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun getCredentials(): CredentialsResult
    suspend fun isAuthenticated(): Boolean = getCredentials() is CredentialsResult.Success
    fun authenticated(): Flow<Boolean>
    suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: suspend (Boolean) -> T
    ): T

    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
}
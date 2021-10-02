package com.vmenon.mpo.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun getCredentials(): CredentialsResult
    fun credentials(): Flow<CredentialsResult>
    suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: suspend (Boolean) -> T
    ): T

    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
    suspend fun didUserLogout(): Boolean
}
package com.vmenon.mpo.auth.domain

import kotlinx.coroutines.flow.Flow

typealias RefreshCredentialsCallback<T> = suspend (Result<Boolean>) -> T
interface AuthService {
    suspend fun getCredentials(): CredentialsResult
    fun credentials(): Flow<CredentialsResult>
    suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: RefreshCredentialsCallback<T>
    ): T

    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
    suspend fun didUserLogout(): Boolean
}
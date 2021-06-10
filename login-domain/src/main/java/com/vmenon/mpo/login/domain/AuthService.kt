package com.vmenon.mpo.login.domain

import kotlinx.coroutines.flow.Flow

interface AuthService {
    fun getCredentials(): Credentials?
    fun isAuthenticated(): Boolean = getCredentials() != null
    fun authenticated(): Flow<Boolean>
    suspend fun <T> retryAndRefreshTokenIfNecessary(comparisonTime: Long, operation: () -> T): T
    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
}
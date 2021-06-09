package com.vmenon.mpo.login.domain

interface AuthService {
    fun getCredentials(): Credentials?
    fun isAuthenticated(): Boolean = getCredentials() != null
    suspend fun <T> retryAndRefreshTokenIfNecessary(comparisonTime: Long, operation: () -> T): T
    suspend fun startAuthentication(context: Any)
    suspend fun logout(context: Any)
}
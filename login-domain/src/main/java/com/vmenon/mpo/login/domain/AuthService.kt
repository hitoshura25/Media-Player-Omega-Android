package com.vmenon.mpo.login.domain

interface AuthService {
    fun getCredentials(): Credentials?
    fun isAuthenticated(): Boolean = getCredentials() != null
    suspend fun startAuthentication(context: Any)
    suspend fun logout()
}
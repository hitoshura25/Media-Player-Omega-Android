package com.vmenon.mpo.auth.framework

interface Authenticator {
    fun startAuthentication(context: Any)
    fun logout(context: Any)
    suspend fun refreshToken(refreshToken: String): Result<Boolean>
}
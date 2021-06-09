package com.vmenon.mpo.login.data

interface Authenticator {
    fun startAuthentication(context: Any)
    fun logout(context: Any)
    suspend fun refreshToken(refreshToken: String)
}
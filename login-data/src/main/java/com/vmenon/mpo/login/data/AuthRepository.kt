package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.AuthService
import com.vmenon.mpo.login.domain.Credentials

class AuthRepository(
    private val authState: AuthState,
    private val authenticator: Authenticator
) : AuthService {
    override fun getCredentials(): Credentials? = authState.getCredentials()
    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun startAuthentication(context: Any) {
        authenticator.startAuthentication(context)
    }
}
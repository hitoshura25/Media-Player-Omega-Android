package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.AuthService
import com.vmenon.mpo.login.domain.Credentials

class AuthRepository(
    private val authState: AuthState,
    private val authenticator: Authenticator
) : AuthService {
    override fun getCredentials(): Credentials? = authState.getCredentials()
    override suspend fun logout(context: Any) {
        authenticator.logout(context)
    }

    override suspend fun startAuthentication(context: Any) {
        authenticator.startAuthentication(context)
    }

    override suspend fun <T> retryAndRefreshTokenIfNecessary(
        comparisonTime: Long,
        operation: () -> T
    ): T {
        val credentials = getCredentials()
        return when {
            credentials == null -> {
                operation()
            }
            credentials.accessTokenExpiration >= comparisonTime + EXPIRATION_WINDOW_MS -> {
                operation()
            }
            else -> {
                authenticator.refreshToken(credentials.refreshToken)
                operation()
            }
        }
    }

    companion object {
        private const val EXPIRATION_WINDOW_MS = 60000L
    }
}
package com.vmenon.mpo.auth.framework

import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.Credentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthServiceImpl(
    private val authState: AuthState,
    private val authenticator: Authenticator
) : AuthService {

    override fun getCredentials(): Credentials? = authState.getCredentials()

    override fun authenticated(): Flow<Boolean> = authState.credentials().map { credentials ->
        credentials != null
    }

    override suspend fun logout(context: Any) {
        authenticator.logout(context)
    }

    override suspend fun startAuthentication(context: Any) {
        authenticator.startAuthentication(context)
    }

    override suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: (Boolean) -> T
    ): T {
        val credentials = getCredentials()
        return when {
            credentials == null -> {
                operation(false)
            }
            credentials.accessTokenExpiration >= comparisonTime + EXPIRATION_WINDOW_MS -> {
                operation(false)
            }
            else -> {
                authenticator.refreshToken(credentials.refreshToken)
                operation(true)
            }
        }
    }

    companion object {
        private const val EXPIRATION_WINDOW_MS = 60000L
    }
}
package com.vmenon.mpo.auth.framework

import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthServiceImpl(
    private val authState: AuthState,
    private val authenticator: Authenticator
) : AuthService {

    override suspend fun getCredentials(): CredentialsResult = authState.getCredentials()

    override fun authenticated(): Flow<Boolean> = authState.credentials().map { credentials ->
        credentials is CredentialsResult.Success
    }

    override suspend fun logout(context: Any) {
        authenticator.logout(context)
    }

    override suspend fun startAuthentication(context: Any) {
        authenticator.startAuthentication(context)
    }

    override suspend fun <T> runWithFreshCredentialsIfNecessary(
        comparisonTime: Long,
        operation: suspend (Boolean) -> T
    ): T {
        return when (val result = getCredentials()) {
            is CredentialsResult.None -> {
                operation(false)
            }
            is CredentialsResult.Success -> {
                if (result.credentials.accessTokenExpiration >= comparisonTime + EXPIRATION_WINDOW_MS) {
                    operation(false)
                } else {
                    authenticator.refreshToken(result.credentials.refreshToken)
                    operation(true)
                }
            }
            else -> {
                operation(false)
            }
        }
    }

    companion object {
        private const val EXPIRATION_WINDOW_MS = 60000L
    }
}
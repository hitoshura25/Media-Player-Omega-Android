package com.vmenon.mpo.auth.framework

import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthServiceImpl(
    private val authState: AuthState,
    private val authenticator: Authenticator,
    private val biometricsManager: BiometricsManager
) : AuthService {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            biometricsManager.promptResponse.collect { response ->
                when (response) {
                    is PromptResponse.DecryptionSuccess -> authState.decryptCredentials(
                        response.decryptionCipher
                    )
                    is PromptResponse.EncryptionSuccess -> authState.encryptCredentials(
                        response.encryptionCipher
                    )
                    else -> {
                    }
                }
            }
        }
    }

    override suspend fun getCredentials(): CredentialsResult = authState.getCredentials()

    override fun credentials(): Flow<CredentialsResult> = authState.credentials()

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
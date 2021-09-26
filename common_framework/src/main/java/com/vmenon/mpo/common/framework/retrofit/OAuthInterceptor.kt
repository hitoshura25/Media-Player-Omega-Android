package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.IllegalStateException
import java.net.HttpURLConnection

/**
 * Will ensure we have a fresh access token prior to making the request. And also will try
 * refreshing again in the event the server says not valid, although locally we think we're
 * valid
 */
class OAuthInterceptor(
    private val authService: AuthService,
    private val logger: Logger,
    private val clock: Clock,
    private val biometricsManager: BiometricsManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val request: Request = chain.request()
            if (authService.isAuthenticated()) {
                handleAuthentication(chain, request)
            } else {
                chain.proceed(request)
            }
        }
    }

    private suspend fun handleAuthentication(chain: Interceptor.Chain, request: Request): Response {
        val response = proceedWithCredentials(chain, request)
        if (response.response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
            && !response.tokenRefreshed
        ) {
            return proceedWithCredentials(chain, response.newRequest).response
        }
        return response.response
    }

    private suspend fun proceedWithCredentials(
        chain: Interceptor.Chain,
        request: Request
    ): ResponseWithCredentials {
        return authService.runWithFreshCredentialsIfNecessary(clock.currentTimeMillis()) { refreshed ->
            logger.println("Refreshed token $refreshed")
            val newRequest = when (val credentialsResult = authService.getCredentials()) {
                is CredentialsResult.Success -> {
                    request.newBuilder().addHeader(
                        "Authorization",
                        "${credentialsResult.credentials.tokenType} ${credentialsResult.credentials.accessToken}"
                    ).build()
                }
                CredentialsResult.None -> request
                CredentialsResult.RequiresBiometricAuth -> {
                    biometricsManager.requestBiometricPrompt(PromptReason.STAY_AUTHENTICATED)
                    throw IllegalStateException("BiometricAuthRequired")
                }
            }
            val response = kotlin.runCatching { chain.proceed(newRequest) }.getOrThrow()
            ResponseWithCredentials(newRequest, response, refreshed)
        }
    }

    private data class ResponseWithCredentials(
        val newRequest: Request,
        val response: Response,
        val tokenRefreshed: Boolean
    )
}
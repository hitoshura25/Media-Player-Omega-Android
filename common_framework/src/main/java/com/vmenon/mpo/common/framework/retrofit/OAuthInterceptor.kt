package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
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
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val request: Request = chain.request()
            if (authService.getCredentials() is CredentialsResult.None) {
                chain.proceed(request)
            } else {
                handleAuthentication(chain, request)
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
                else -> request
                // TODO: What if Biometric prompt is needed?
                // Maybe implement an EventBus, and send an event notifying the listener (the HomeActivity maybe?)
                // That we need to prompt for biometrics to stay authenticated. Then throw an error and hope
                // the retry mechanism will eventually get it? Or maybe just do this from the AuthService??
            }
            val response = runCatching { chain.proceed(newRequest) }.getOrThrow()
            ResponseWithCredentials(newRequest, response, refreshed)
        }
    }

    private data class ResponseWithCredentials(
        val newRequest: Request,
        val response: Response,
        val tokenRefreshed: Boolean
    )
}
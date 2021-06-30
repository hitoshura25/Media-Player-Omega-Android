package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.auth.domain.AuthService
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
    private val clock: Clock
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        return if (authService.isAuthenticated()) {
            handleAuthentication(chain, request)
        } else {
            chain.proceed(request)
        }
    }

    private fun handleAuthentication(chain: Interceptor.Chain, request: Request): Response {
        val response = proceedWithCredentials(chain, request)
        if (response.response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
            && !response.tokenRefreshed
        ) {
            return proceedWithCredentials(chain, response.newRequest).response
        }
        return response.response
    }

    private fun proceedWithCredentials(
        chain: Interceptor.Chain,
        request: Request
    ): ResponseWithCredentials = runBlocking {
        authService.runWithFreshCredentialsIfNecessary(clock.currentTimeMillis()) { refreshed ->
            logger.println("Refreshed token $refreshed")
            val credentials = authService.getCredentials()
            val newRequest = if (credentials != null) {
                request.newBuilder().addHeader(
                    "Authorization",
                    "${credentials.tokenType} ${credentials.accessToken}"
                ).build()
            } else {
                request
            }
            val response = chain.proceed(newRequest)
            ResponseWithCredentials(newRequest, response, refreshed)
        }
    }

    private data class ResponseWithCredentials(
        val newRequest: Request,
        val response: Response,
        val tokenRefreshed: Boolean
    )
}
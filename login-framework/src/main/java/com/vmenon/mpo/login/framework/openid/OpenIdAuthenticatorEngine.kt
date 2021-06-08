package com.vmenon.mpo.login.framework.openid

import android.app.Activity
import android.net.Uri
import androidx.core.app.ActivityCompat.startActivityForResult
import com.vmenon.mpo.login.domain.Credentials
import com.vmenon.mpo.login.framework.BuildConfig
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationRequest.Scope
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class OpenIdAuthenticatorEngine {
    private var serviceConfiguration: AuthorizationServiceConfiguration? = null
    private var authorizationService: AuthorizationService? = null

    fun initialize(context: Activity) {
        authorizationService = AuthorizationService(context)
    }

    fun cleanup() {
        authorizationService?.dispose()
        authorizationService = null
    }

    suspend fun performAuthenticate(context: Activity) {
        authorizationService?.let { authorizationService ->
            val authRequest = AuthorizationRequest.Builder(
                getServiceConfiguration(),
                BuildConfig.OAUTH_CLIENT_ID,
                ResponseTypeValues.CODE,
                REDIRECT_URI
            ).setScopes(
                Scope.OPENID,
                Scope.OFFLINE_ACCESS,
                Scope.PROFILE
            ).build()

            context.startActivityForResult(
                authorizationService.getAuthorizationRequestIntent(authRequest),
                RC_AUTH
            )
        }
    }

    suspend fun performLogout(context: Activity, idToken:String) {
        authorizationService?.let { authorizationService ->
            val endSessionRequest = EndSessionRequest.Builder(getServiceConfiguration())
                .setIdToken(idToken)
                .setRedirectUri()
                .build()
            val endSessionIntent =
                authorizationService.getEndSessionRequestIntent(endSessionRequest)
            context.startActivityForResult(endSessionIntent, RC_LOGOUT)
        }

    }

    suspend fun handleAuthResponse(
        authorizationResponse: AuthorizationResponse?,
        authorizationException: AuthorizationException?
    ): Credentials {
        if (authorizationResponse != null) {
            return performTokenRequest(authorizationResponse)
        } else if (authorizationException != null) {
            handleAuthError(authorizationException)
        }

        throw IllegalStateException("No Auth response or exception")
    }

    private suspend fun performTokenRequest(
        authorizationResponse: AuthorizationResponse
    ): Credentials =
        suspendCoroutine { continuation ->
            authorizationService?.let { authorizationService ->
                authorizationService.performTokenRequest(
                    authorizationResponse.createTokenExchangeRequest()
                ) { response, exception ->
                    when {
                        response != null -> {
                            continuation.resume(handleTokenResponse(response))
                        }
                        exception != null -> {
                            handleTokenError(exception)
                        }
                        else -> {
                            continuation.resumeWithException(
                                IllegalStateException("No Auth response or exception")
                            )
                        }
                    }
                }
            } ?: continuation.resumeWithException(
                IllegalStateException("No AuthorizationService")
            )
        }

    private fun handleTokenResponse(response: TokenResponse): Credentials {
        val accessToken = response.accessToken
        val accessTokenExpiration = response.accessTokenExpirationTime
        val refreshToken = response.refreshToken
        val idToken = response.idToken
        val tokenType = response.tokenType
        return if (
            accessToken != null
            && accessTokenExpiration != null
            && refreshToken != null
            && idToken != null
            && tokenType != null
        ) {
            Credentials(
                accessToken = accessToken,
                accessTokenExpiration = accessTokenExpiration,
                refreshToken = refreshToken,
                idToken = idToken,
                tokenType = tokenType
            )
        } else {
            throw IllegalStateException("Invalid auth response")
        }
    }

    private fun handleAuthError(exception: AuthorizationException) {
        throw exception
    }

    private fun handleTokenError(exception: AuthorizationException) {
        throw exception
    }

    private suspend fun getServiceConfiguration(): AuthorizationServiceConfiguration =
        suspendCoroutine { continuation ->
            serviceConfiguration.let { storedConfiguration ->
                if (storedConfiguration != null) {
                    continuation.resume(storedConfiguration)
                } else {
                    AuthorizationServiceConfiguration.fetchFromIssuer(DISCOVERY_URI)
                    { configuration: AuthorizationServiceConfiguration?,
                      exception: AuthorizationException? ->
                        if (exception != null) {
                            continuation.resumeWithException(exception)
                        }

                        if (configuration != null) {
                            serviceConfiguration = configuration
                            continuation.resume(configuration)
                        }
                    }
                }
            }
        }

    companion object {
        private val DISCOVERY_URI =
            Uri.parse("https://dev-00189988.okta.com/oauth2/default")
        private val REDIRECT_URI =
            Uri.parse("${BuildConfig.APP_AUTH_REDIRECT_SCHEME}:/oauth/callback")
        private val LOREDIRECT_URI =
            Uri.parse("${BuildConfig.APP_AUTH_REDIRECT_SCHEME}:/oauth/callback")
        const val RC_AUTH = 4000
        const val RC_LOGOUT = 4001
    }
}
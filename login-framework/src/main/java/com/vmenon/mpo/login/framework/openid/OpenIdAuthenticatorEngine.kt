package com.vmenon.mpo.login.framework.openid

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.vmenon.mpo.login.domain.Credentials
import com.vmenon.mpo.login.framework.BuildConfig
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationRequest.Scope
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.NoClientAuthentication
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OpenIdAuthenticatorEngine(context: Context) {
    private var serviceConfiguration: AuthorizationServiceConfiguration? = null
    private var authorizationService = AuthorizationService(context.applicationContext)

    suspend fun performAuthenticate(launcher: ActivityResultLauncher<Intent>) {
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

        launcher.launch(authorizationService.getAuthorizationRequestIntent(authRequest))
    }

    suspend fun performLogout(launcher: ActivityResultLauncher<Intent>, idToken: String) {
        val endSessionRequest = EndSessionRequest.Builder(
            getServiceConfiguration(),
            idToken,
            LOGOUT_REDIRECT_URI
        ).build()
        val endSessionIntent =
            authorizationService.getEndSessionRequestIntent(endSessionRequest)
        launcher.launch(endSessionIntent)
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

    suspend fun handleEndSessionResponse(
        endSessionResponse: EndSessionResponse?,
        exception: AuthorizationException?
    ) {
        if (exception != null) {
            println("End Session error: $exception")
        }
    }

    suspend fun refreshToken(refreshToken: String): Credentials {
        val request = TokenRequest.Builder(
            getServiceConfiguration(),
            BuildConfig.OAUTH_CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScope(null)
            .setRefreshToken(refreshToken)
            .build()

        return performRefreshTokenRequest(request)
    }

    private suspend fun performTokenRequest(
        authorizationResponse: AuthorizationResponse
    ): Credentials =
        suspendCoroutine { continuation ->
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
        }


    private suspend fun performRefreshTokenRequest(tokenRequest: TokenRequest): Credentials =
        suspendCoroutine { continuation ->
            authorizationService.performTokenRequest(
                tokenRequest,
                NoClientAuthentication.INSTANCE
            ) { response, ex ->
                if (ex != null) {
                    handleTokenError(ex)
                }

                if (response != null) {
                    continuation.resume(handleTokenResponse(response))
                }
            }
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
            throw IllegalStateException("Invalid token response")
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
        private val LOGOUT_REDIRECT_URI =
            Uri.parse("${BuildConfig.APP_AUTH_REDIRECT_SCHEME}:/oauth/logoutCallback")
        const val RC_AUTH = 4000
        const val RC_LOGOUT = 4001
    }
}
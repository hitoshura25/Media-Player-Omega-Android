package com.vmenon.mpo.login.framework.openid

import android.app.Activity
import android.content.Context
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.EndSessionResponse

class OpenIdAuthenticator(
    applicationContext: Context,
    private val authState: AuthState
) : Authenticator {
    private val authenticatorEngine = OpenIdAuthenticatorEngine(applicationContext)

    override fun startAuthentication(context: Any) {
        if (context is Activity) {
            // Just launch the OpenIdHandlerActivity
            context.startActivity(OpenIdHandlerActivity.createPerformAuthIntent(context))
        } else {
            throw IllegalStateException("Context for ${javaClass.name} needs to be an Activity!")
        }
    }

    override fun logout(context: Any) {
        if (context is Activity) {
            // Just launch the OpenIdHandlerActivity
            context.startActivity(OpenIdHandlerActivity.createLogOutIntent(context))
        } else {
            throw IllegalStateException("Context for ${javaClass.name} needs to be an Activity!")
        }
    }

    override suspend fun refreshToken(refreshToken: String) {
        val credentials = authenticatorEngine.refreshToken(refreshToken)
        authState.storeCredentials(credentials)
    }

    fun initialize(activity: Activity) {
        authenticatorEngine.initialize(activity)
    }

    fun cleanup() {
        authenticatorEngine.cleanup()
    }

    suspend fun performAuthenticate(activity: Activity) {
        authenticatorEngine.performAuthenticate(activity)
    }

    suspend fun performLogoutIfNecessary(activity: Activity): Boolean {
        val credentials = authState.getCredentials()
        return if (credentials != null) {
            authenticatorEngine.performLogout(activity, credentials.idToken)
            true
        } else {
            false
        }
    }

    suspend fun handleAuthResponse(
        response: AuthorizationResponse?,
        exception: AuthorizationException?
    ) {
        val credentials = authenticatorEngine.handleAuthResponse(response, exception)
        authState.storeCredentials(credentials)
    }

    suspend fun handleEndSessionResponse(
        response: EndSessionResponse?,
        exception: AuthorizationException?
    ) {
        authenticatorEngine.handleEndSessionResponse(response, exception)
        authState.clearCredentials()
    }
}



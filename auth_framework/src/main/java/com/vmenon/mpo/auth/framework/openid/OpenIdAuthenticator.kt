package com.vmenon.mpo.auth.framework.openid

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment
import com.vmenon.mpo.system.domain.Logger
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.EndSessionResponse

class OpenIdAuthenticator(
    applicationContext: Context,
    private val authState: AuthState,
    logger: Logger
) : Authenticator {
    private val authenticatorEngine = OpenIdAuthenticatorEngine(applicationContext, logger)

    override fun startAuthentication(context: Any) {
        if (context is FragmentActivity) {
            val fragment = OpenIdHandlerFragment.forAuthentication()
            context.supportFragmentManager.beginTransaction()
                .add(fragment, fragment.javaClass.name)
                .commit()
        } else {
            throw IllegalStateException("Context for ${javaClass.name} needs to be an Activity!")
        }
    }

    override fun logout(context: Any) {
        if (context is FragmentActivity) {
            val fragment = OpenIdHandlerFragment.forLogOut()
            context.supportFragmentManager.beginTransaction()
                .add(fragment, fragment.javaClass.name)
                .commit()
        } else {
            throw IllegalStateException("Context for ${javaClass.name} needs to be an Activity!")
        }
    }

    override suspend fun refreshToken(refreshToken: String) {
        val credentials = authenticatorEngine.refreshToken(refreshToken)
        authState.storeCredentials(credentials)
    }

    suspend fun performAuthenticate(launcher: ActivityResultLauncher<Intent>) {
        authenticatorEngine.performAuthenticate(launcher)
    }

    suspend fun performLogoutIfNecessary(launcher: ActivityResultLauncher<Intent>): Boolean {
        val credentialsResult = authState.getCredentials()
        return if (credentialsResult is CredentialsResult.Success) {
            authenticatorEngine.performLogout(launcher, credentialsResult.credentials.idToken)
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



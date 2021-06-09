package com.vmenon.mpo.login.framework.openid.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.framework.openid.OpenIdAuthenticator
import com.vmenon.mpo.login.framework.openid.OpenIdAuthenticatorEngine
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity.Companion.EXTRA_OPERATION
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity.Companion.Operation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.EndSessionResponse
import javax.inject.Inject

class OpenIdHandlerViewModel : ViewModel() {
    @Inject
    lateinit var authenticator: Authenticator

    private val authenticated = MutableLiveData<Boolean>()

    fun authenticated(): LiveData<Boolean> = authenticated

    fun onCreated(activity: Activity) {
        (authenticator as OpenIdAuthenticator).initialize(activity)
    }

    fun onResume(activity: Activity) {
        val operation = activity.intent.getSerializableExtra(EXTRA_OPERATION) as Operation?
        println("Operation $operation")
        if (operation != null) {
            when (operation) {
                Operation.PERFORM_AUTH -> handlePerformAuthOperationRequest(activity)
                Operation.LOGOUT -> handleEndSessionRequest(activity)
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OpenIdAuthenticatorEngine.RC_AUTH -> handlePerformAuthOperationResult(data)
                OpenIdAuthenticatorEngine.RC_LOGOUT -> handleEndSessionOperationResult(data)
            }
        }
    }

    private fun handlePerformAuthOperationRequest(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            activity.intent.removeExtra(EXTRA_OPERATION)
            (authenticator as OpenIdAuthenticator).performAuthenticate(activity)
        }
    }

    private fun handleEndSessionRequest(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            activity.intent.removeExtra(EXTRA_OPERATION)
            if (!(authenticator as OpenIdAuthenticator).performLogoutIfNecessary(activity)) {
                authenticated.postValue(false) // Already logged out I Guess
            }
        }
    }

    private fun handlePerformAuthOperationResult(data: Intent?) {
        if (data != null) {
            viewModelScope.launch(Dispatchers.IO) {
                (authenticator as OpenIdAuthenticator).handleAuthResponse(
                    AuthorizationResponse.fromIntent(data),
                    AuthorizationException.fromIntent(data)
                )
                authenticated.postValue(true)
            }
        }
    }

    private fun handleEndSessionOperationResult(data: Intent?) {
        if (data != null) {
            viewModelScope.launch(Dispatchers.IO) {
                (authenticator as OpenIdAuthenticator).handleEndSessionResponse(
                    EndSessionResponse.fromIntent(data),
                    AuthorizationException.fromIntent(data)
                )
                authenticated.postValue(false)
            }
        }
    }

    fun onDestroyed() {
        (authenticator as OpenIdAuthenticator).cleanup()
    }
}
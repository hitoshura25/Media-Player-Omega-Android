package com.vmenon.mpo.login.framework.openid.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.framework.openid.OpenIdAuthenticatorEngine
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity.Companion.EXTRA_OPERATION
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity.Companion.Operation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

class OpenIdHandlerViewModel : ViewModel() {

    @Inject
    lateinit var authState: AuthState

    private val authenticatorEngine = OpenIdAuthenticatorEngine()
    private val authenticated = MutableLiveData<Boolean>()

    fun authenticated(): LiveData<Boolean> = authenticated

    fun onCreated(activity: Activity) {
        authenticatorEngine.initialize(activity)
    }

    fun onResume(activity: Activity) {
        val operation = activity.intent.getSerializableExtra(EXTRA_OPERATION) as Operation?
        println("Operation $operation")
        if (operation != null) {
            when (operation) {
                Operation.PERFORM_AUTH -> viewModelScope.launch(Dispatchers.IO) {
                    activity.intent.removeExtra(EXTRA_OPERATION)
                    authenticatorEngine.performAuthenticate(activity)
                }
                Operation.LOGOUT -> {
                    activity.intent.removeExtra(EXTRA_OPERATION)

                }
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OpenIdAuthenticatorEngine.RC_AUTH
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val credentials = authenticatorEngine.handleAuthResponse(
                    AuthorizationResponse.fromIntent(data),
                    AuthorizationException.fromIntent(data)
                )
                authState.storeCredentials(credentials)
                authenticated.postValue(true)
            }
        }
    }

    fun onDestroyed() {
        authenticatorEngine.cleanup()
    }
}
package com.vmenon.mpo.auth.framework.openid.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.auth.framework.openid.OpenIdAuthenticator
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment.Companion.EXTRA_OPERATION
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment.Companion.Operation
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.EndSessionResponse
import javax.inject.Inject

class OpenIdHandlerViewModel : ViewModel() {
    @Inject
    lateinit var authenticator: Authenticator

    @Inject
    lateinit var logger: Logger

    private val authenticated = MutableLiveData<Boolean>()
    private var startAuthContract: ActivityResultLauncher<Intent>? = null
    private var logoutContract: ActivityResultLauncher<Intent>? = null

    fun authenticated(): LiveData<Boolean> = authenticated

    fun onCreated(fragment: Fragment) {
        startAuthContract = fragment.registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            handlePerformAuthOperationResult(activityResult.resultCode, activityResult.data)
        }
        logoutContract = fragment.registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            handleEndSessionOperationResult(activityResult.resultCode, activityResult.data)
        }
    }

    fun onResume(fragment: Fragment) {
        val operation =
            fragment.arguments?.getSerializable(EXTRA_OPERATION) as Operation?
        if (operation != null) {
            fragment.arguments?.remove(EXTRA_OPERATION)
            when (operation) {
                Operation.PERFORM_AUTH -> handlePerformAuthOperationRequest(
                    startAuthContract!!
                )
                Operation.LOGOUT -> handleEndSessionRequest(
                    logoutContract!!
                )
            }
        }
    }

    private fun handlePerformAuthOperationRequest(launcher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch(Dispatchers.IO) {
            (authenticator as OpenIdAuthenticator).performAuthenticate(launcher)
        }
    }

    private fun handleEndSessionRequest(launcher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!(authenticator as OpenIdAuthenticator).performLogoutIfNecessary(launcher)) {
                authenticated.postValue(false) // Already logged out I Guess
            }
        }
    }

    private fun handlePerformAuthOperationResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            viewModelScope.launch(Dispatchers.IO) {
                (authenticator as OpenIdAuthenticator).handleAuthResponse(
                    AuthorizationResponse.fromIntent(data),
                    AuthorizationException.fromIntent(data)
                )
                authenticated.postValue(true)
            }
        } else {
            logger.println("Issue with handling auth result $resultCode $data")
            authenticated.postValue(false)
        }
    }

    private fun handleEndSessionOperationResult(resultCode: Int, data: Intent?) {
        var response: EndSessionResponse? = null
        var exception: AuthorizationException? = null
        if (resultCode == Activity.RESULT_OK && data != null) {
            response = EndSessionResponse.fromIntent(data)
            exception = AuthorizationException.fromIntent(data)
        }

        viewModelScope.launch(Dispatchers.IO) {
            (authenticator as OpenIdAuthenticator).handleEndSessionResponse(
                response,
                exception
            )
            authenticated.postValue(false)
        }
    }
}
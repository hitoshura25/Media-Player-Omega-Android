package com.vmenon.mpo.auth.framework.openid.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.VisibleForTesting
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.lang.IllegalStateException
import javax.inject.Inject

class OpenIdHandlerViewModel : ViewModel() {
    @Inject
    lateinit var authenticator: Authenticator

    @Inject
    lateinit var logger: Logger

    private val authenticated = MutableLiveData<Boolean>()

    @VisibleForTesting
    internal var dispatcher: CoroutineDispatcher = Dispatchers.IO

    @VisibleForTesting
    internal var startAuthContract: ActivityResultLauncher<Intent>? = null

    @VisibleForTesting
    internal var logoutContract: ActivityResultLauncher<Intent>? = null

    fun authenticated(): LiveData<Boolean> = authenticated

    fun onCreated(fragment: Fragment) {
        startAuthContract = fragment.registerForActivityResult(
            StartActivityForResult()
        ) { activityResult ->
            handlePerformAuthOperationResult(activityResult.resultCode, activityResult.data)
        }
        logoutContract = fragment.registerForActivityResult(
            StartActivityForResult()
        ) { activityResult ->
            handleEndSessionOperationResult(activityResult.resultCode, activityResult.data)
        }
    }

    fun onResume(fragment: Fragment) {
        val arguments = fragment.arguments ?: return
        val operation = arguments.getSerializable(EXTRA_OPERATION) as Operation?
        if (operation != null) {
            arguments.remove(EXTRA_OPERATION)
            when (operation) {
                Operation.PERFORM_AUTH -> startAuthContract?.let { contract ->
                    handlePerformAuthOperationRequest(contract)
                } ?: throw IllegalStateException("startAuthContract should not be null!")

                Operation.LOGOUT -> logoutContract?.let { contract ->
                    handleEndSessionRequest(contract)
                } ?: throw IllegalStateException("logoutContract should not be null!")
            }
        }
    }

    private fun handlePerformAuthOperationRequest(launcher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch(dispatcher) {
            (authenticator as OpenIdAuthenticator).performAuthenticate(launcher)
        }
    }

    private fun handleEndSessionRequest(launcher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch(dispatcher) {
            if (!(authenticator as OpenIdAuthenticator).performLogoutIfNecessary(launcher)) {
                authenticated.postValue(false) // Already logged out I Guess
            }
        }
    }

    private fun handlePerformAuthOperationResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            viewModelScope.launch(dispatcher) {
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
        var exception: AuthorizationException? = null
        if (resultCode == Activity.RESULT_OK && data != null) {
            exception = AuthorizationException.fromIntent(data)
        }

        viewModelScope.launch(dispatcher) {
            (authenticator as OpenIdAuthenticator).handleEndSessionResponse(
                exception
            )
            authenticated.postValue(false)
        }
    }
}
package com.vmenon.mpo.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.navigation.domain.NavigationController
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var biometricsManager: BiometricsManager

    @Inject
    lateinit var authService: AuthService

    val currentLocation = liveData {
        emitSource(navigationController.currentLocation.asLiveData())
    }

    fun promptForBiometricsToStayAuthenticated(fragment: Fragment) {
        viewModelScope.launch {
            val credentialsResult = authService.getCredentials()
            if (credentialsResult is CredentialsResult.RequiresBiometricAuth) {
                biometricsManager.requestBiometricPrompt(
                    fragment,
                    PromptRequest(
                        reason = PromptReason.Decryption(credentialsResult.encryptedData),
                        title = fragment.getString(R.string.authenticate),
                        subtitle = fragment.getString(R.string.confirm_to_stay_authenticated),
                        confirmationRequired = false,
                        negativeActionText = fragment.getString(R.string.logout)
                    ))
            }
        }
    }
}
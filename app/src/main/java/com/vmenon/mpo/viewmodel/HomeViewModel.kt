package com.vmenon.mpo.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.navigation.domain.NavigationController
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var biometricsManager: BiometricsManager

    val currentLocation = liveData {
        emitSource(navigationController.currentLocation.asLiveData())
    }
    val biometricPromptRequested = liveData {
        emitSource(biometricsManager.biometricPromptRequested.asLiveData())
    }

    fun showBiometricPrompt(activity: AppCompatActivity, request: PromptRequest) {
        biometricsManager.showBiometricPrompt(activity, request)
    }
}
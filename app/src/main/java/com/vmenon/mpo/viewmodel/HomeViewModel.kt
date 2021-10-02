package com.vmenon.mpo.viewmodel

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult.RequiresBiometricAuth
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

    var biometricEnrollmentLauncher: ActivityResultLauncher<Intent>? = null

    private val _promptForBiometricsToStayAuthenticated = MutableLiveData<Unit>()
    val promptForBiometricsToStayAuthenticated: LiveData<Unit> =
        _promptForBiometricsToStayAuthenticated

    fun registerForBiometricEnrollment(activity: AppCompatActivity) {
        biometricEnrollmentLauncher = activity.registerForActivityResult(StartActivityForResult()) {

        }
        biometricsManager.enrollmentRequired.asLiveData().observe(activity) {
            promptForBiometricEnrollment()
        }

        authService.credentials().asLiveData().observe(activity) { credentialsResult ->
            viewModelScope.launch {
                if (credentialsResult is RequiresBiometricAuth && !authService.isLoggedOut()) {
                    _promptForBiometricsToStayAuthenticated.postValue(Unit)
                }
            }
        }
    }

    fun promptForBiometricsToStayAuthenticated(activity: AppCompatActivity) {
        viewModelScope.launch {
            val credentialsResult = authService.getCredentials()
            if (credentialsResult is RequiresBiometricAuth) {
                biometricsManager.requestBiometricPrompt(
                    activity,
                    PromptRequest(
                        reason = PromptReason.Decryption(credentialsResult.encryptedData),
                        title = activity.getString(R.string.authenticate),
                        subtitle = activity.getString(R.string.confirm_to_stay_authenticated),
                        confirmationRequired = false,
                        negativeActionText = activity.getString(R.string.logout)
                    )
                )
            }
        }
    }

    private fun promptForBiometricEnrollment() {
        val enrollIntent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                Intent(Settings.ACTION_FINGERPRINT_ENROLL)
            }
            else -> {
                Intent(Settings.ACTION_SECURITY_SETTINGS)
            }
        }
        biometricEnrollmentLauncher?.launch(enrollIntent)
    }
}
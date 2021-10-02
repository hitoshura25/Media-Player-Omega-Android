package com.vmenon.mpo.viewmodel

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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

    var biometricEnrollmentLauncher: ActivityResultLauncher<Intent>? = null

    fun registerForBiometricEnrollment(activity: AppCompatActivity) {
        biometricEnrollmentLauncher = activity.registerForActivityResult(StartActivityForResult()) {

        }
        biometricsManager.enrollmentRequired.asLiveData().observe(activity) {
            promptForBiometricEnrollment()
        }
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
package com.vmenon.mpo.viewmodel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.lifecycle.*
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.CredentialsResult.RequiresBiometricAuth
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.model.BiometricsState
import com.vmenon.mpo.model.BiometricsState.*
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

    private val biometricPromptRequestAfterEnrollment = MutableLiveData<PromptRequest>()
    private var biometricEnrollmentLauncher: ActivityResultLauncher<Intent>? = null
    private var biometricRequestWaitingOnEnrollment: PromptRequest? = null

    private val biometricsStateMediator by lazy {
        MediatorLiveData<ContentEvent<BiometricsState>>().apply {
            addSource(authService.credentials().asLiveData()) { credentialsResult ->
                if (credentialsResult is RequiresBiometricAuth) {
                    viewModelScope.launch {
                        if (!authService.didUserLogout()) {
                            postValue(
                                PromptToStayAuthenticated(
                                    biometricsManager.enrollmentRequired()
                                ).toContentEvent()
                            )
                        }
                    }
                }
            }
            addSource(biometricsManager.promptToEnroll.asLiveData()) { request ->
                postValue(PromptToEnroll(request).toContentEvent())
            }
            addSource(biometricPromptRequestAfterEnrollment) { request ->
                postValue(PromptAfterEnrollment(request).toContentEvent())
            }
        }
    }

    fun registerForBiometrics(activity: AppCompatActivity): LiveData<ContentEvent<BiometricsState>> {
        biometricEnrollmentLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                biometricRequestWaitingOnEnrollment?.let { request ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        biometricPromptRequestAfterEnrollment.postValue(request)
                    }
                    biometricRequestWaitingOnEnrollment = null
                }
            }
        return biometricsStateMediator
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

    @Suppress("DEPRECATION")
    fun promptForBiometricEnrollment(request: PromptRequest) {
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
        biometricRequestWaitingOnEnrollment = request
        biometricEnrollmentLauncher?.launch(enrollIntent)
    }

    fun promptForBiometricsAfterEnrollment(activity: AppCompatActivity, request: PromptRequest) {
        biometricsManager.requestBiometricPrompt(activity, request)
    }
}
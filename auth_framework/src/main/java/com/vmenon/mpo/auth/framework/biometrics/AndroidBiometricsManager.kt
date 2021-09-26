package com.vmenon.mpo.auth.framework.biometrics

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vmenon.mpo.auth.domain.biometrics.BiometricState
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.auth.framework.CryptographyManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.crypto.Cipher

class AndroidBiometricsManager(context: Context) : BiometricsManager {
    private val appContext = context.applicationContext
    private val cipher = MutableSharedFlow<Cipher>()
    private val cryptographyManager = CryptographyManager()
    private val _biometricPromptRequested = MutableSharedFlow<PromptReason>()

    override val biometricPromptRequested: Flow<PromptReason> = _biometricPromptRequested

    override fun biometricState(): BiometricState =
        when (BiometricManager.from(appContext).canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricState.SUCCESS
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricState.REQUIRES_ENROLLMENT
            else -> BiometricState.NOT_SUPPORTED
        }

    override fun <T : Any> showBiometricPrompt(requester: T, request: PromptRequest) {
        when (requester) {
            is AppCompatActivity -> {
                handleBiometricFlow(requester, request)
            }
            is Fragment -> {
                handleBiometricFlow(requester.requireActivity() as AppCompatActivity, request)
            }
            else -> {
                throw IllegalArgumentException(
                    "requester type ${requester::class.qualifiedName} is not supported"
                )
            }
        }
    }

    override fun cipher(): Flow<Cipher> = cipher

    override suspend fun requestBiometricPrompt(reason: PromptReason) {
        _biometricPromptRequested.emit(reason)
    }

    private fun handleBiometricFlow(activity: AppCompatActivity, request: PromptRequest) {
        when (biometricState()) {
            BiometricState.SUCCESS -> {
                val secretKeyName = "biometric_sample_encryption_key"
                val cipher =
                    cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
                val biometricPrompt = createBiometricPrompt(activity, ::handleBiometricSuccess)
                val promptInfo = createPromptInfo(request)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
            BiometricState.REQUIRES_ENROLLMENT -> promptToEnrollInBiometrics(activity, request)
            BiometricState.NOT_SUPPORTED -> {

            }
        }
    }

    @Suppress("DEPRECATION")
    private fun promptToEnrollInBiometrics(activity: AppCompatActivity, request: PromptRequest) {
        val enrollIntent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_WEAK
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
        val biometricEnrollmentContract = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                handleBiometricFlow(activity, request)
            }
        }
        biometricEnrollmentContract.launch(enrollIntent)
    }

    private fun handleBiometricSuccess(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.let { cryptoObject ->
            cryptoObject.cipher?.let { biometricCipher ->
                cipher.tryEmit(biometricCipher)
            }
        }
    }

    private fun createBiometricPrompt(
        activity: AppCompatActivity,
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                Log.d(TAG, "errCode is $errCode and errString is: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                processSuccess(result)
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    private fun createPromptInfo(request: PromptRequest): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(request.title)
            setConfirmationRequired(request.confirmationRequired)
            request.subtitle?.let { subtitle ->
                setSubtitle(subtitle)
            }
            request.description?.let { description ->
                setDescription(description)
            }
            request.negativeActionText?.let { negativeActionText ->
                setNegativeButtonText(negativeActionText)
            }
        }.build()

    companion object {
        private const val TAG = "AndroidBiometricsManager"
    }
}
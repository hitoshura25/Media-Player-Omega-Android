package com.vmenon.mpo.auth.framework.biometrics

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vmenon.mpo.auth.domain.CipherEncryptedData
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason.*
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.auth.domain.biometrics.PromptResponse
import com.vmenon.mpo.auth.domain.biometrics.PromptResponse.DecryptionSuccess
import com.vmenon.mpo.auth.domain.biometrics.PromptResponse.EncryptionSuccess
import com.vmenon.mpo.auth.framework.CryptographyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AndroidBiometricsManager(context: Context) : BiometricsManager {
    private val appContext = context.applicationContext
    private val cryptographyManager = CryptographyManager()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val promptResponse = MutableSharedFlow<PromptResponse>()
    override val promptToEnroll = MutableSharedFlow<PromptRequest>()

    override fun deviceSupportsBiometrics(): Boolean =
        when (BiometricManager.from(appContext).canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }

    override fun enrollmentRequired(): Boolean = BiometricManager.from(appContext)
        .canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

    override fun <T : Any> requestBiometricPrompt(requester: T, request: PromptRequest) {
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

    private fun handleBiometricFlow(activity: AppCompatActivity, request: PromptRequest) {
        when (BiometricManager.from(appContext).canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                when (request.reason) {
                    is Encryption -> handleBiometricFlowForEncryption(activity, request)
                    is Decryption ->
                        handleBiometricFlowForDecryption(
                            activity,
                            request,
                            (request.reason as Decryption).cipherEncryptedData
                        )
                    Confirmation -> {

                    }
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> scope.launch {
                promptToEnroll.emit(request)
            }
            else -> {

            }
        }
    }

    private fun handleBiometricFlowForDecryption(
        activity: AppCompatActivity,
        request: PromptRequest,
        cipherEncryptedData: CipherEncryptedData
    ) {
        val cipher = cryptographyManager.getInitializedCipherForDecryption(
            secretKeyName,
            cipherEncryptedData.initializationVector
        )
        val biometricPrompt = createBiometricPrompt(activity) { authResult ->
            authResult.cryptoObject?.let { cryptoObject ->
                cryptoObject.cipher?.let { biometricCipher ->
                    scope.launch {
                        promptResponse.emit(DecryptionSuccess(request, biometricCipher))
                    }
                }
            }
        }
        val promptInfo = createPromptInfo(request)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun handleBiometricFlowForEncryption(
        activity: AppCompatActivity,
        request: PromptRequest
    ) {
        val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
        val biometricPrompt = createBiometricPrompt(activity) { authResult ->
            authResult.cryptoObject?.let { cryptoObject ->
                cryptoObject.cipher?.let { biometricCipher ->
                    scope.launch {
                        promptResponse.emit(EncryptionSuccess(request, biometricCipher))
                    }
                }
            }
        }
        val promptInfo = createPromptInfo(request)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
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
        private const val secretKeyName = "biometric_sample_encryption_key"
    }
}
package com.vmenon.mpo.auth.framework.biometrics

import android.content.Context
import androidx.annotation.VisibleForTesting
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
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.crypto.Cipher

open class AndroidBiometricsManager(context: Context, private val logger: Logger) :
    BiometricsManager {
    private val appContext = context.applicationContext
    private val cryptographyManager = CryptographyManager()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @VisibleForTesting
    protected open val userAuthenticationRequired: Boolean = true

    override val promptResponse = MutableSharedFlow<PromptResponse>()
    override val promptToEnroll = MutableSharedFlow<PromptRequest>()

    override fun deviceSupportsBiometrics(): Boolean =
        when (canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }

    override fun enrollmentRequired(): Boolean =
        canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

    @Suppress("SameParameterValue")
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

    @Suppress("SameParameterValue")
    protected open fun canAuthenticate(authType: Int) =
        BiometricManager.from(appContext).canAuthenticate(authType)

    protected open fun authenticate(
        activity: AppCompatActivity,
        request: PromptRequest,
        cipher: Cipher,
        processSuccess: (BiometricPrompt.CryptoObject?) -> Unit
    ) {
        val biometricPrompt = createBiometricPrompt(activity, processSuccess)
        val promptInfo = createPromptInfo(request)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun handleBiometricFlow(activity: AppCompatActivity, request: PromptRequest) {
        logger.println("AndroidBiometrics::handleBiometricFlow")
        when (canAuthenticate(BIOMETRIC_WEAK)) {
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
                logger.println("AndroidBiometricsManager::promptToEnroll.emit(${request.reason})")
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
        logger.println("AndroidBiometricsManager::handleBiometricFlowForDecryption(${request.reason})")
        val cipher = cryptographyManager.getInitializedCipherForDecryption(
            secretKeyName,
            cipherEncryptedData.initializationVector,
            userAuthenticationRequired
        )
        authenticate(activity, request, cipher) { cryptoObject ->
            cryptoObject?.cipher?.let { biometricCipher ->
                scope.launch {
                    promptResponse.emit(DecryptionSuccess(request, biometricCipher))
                }
            }
        }
    }

    private fun handleBiometricFlowForEncryption(
        activity: AppCompatActivity,
        request: PromptRequest
    ) {
        logger.println("AndroidBiometricsManager::handleBiometricFlowForEncryption(${request.reason})")
        val cipher = cryptographyManager.getInitializedCipherForEncryption(
            secretKeyName,
            userAuthenticationRequired
        )
        authenticate(activity, request, cipher) { cryptoObject ->
            cryptoObject?.cipher?.let { biometricCipher ->
                scope.launch {
                    promptResponse.emit(EncryptionSuccess(request, biometricCipher))
                }
            }
        }
    }

    private fun createBiometricPrompt(
        activity: AppCompatActivity,
        processSuccess: (BiometricPrompt.CryptoObject?) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                logger.println("AndroidBiometricsManager::errCode is $errCode and errString is: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                logger.println("AndroidBiometricsManager::User biometric rejected.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                logger.println("AndroidBiometricsManager::Authentication was successful")
                processSuccess(result.cryptoObject)
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
        private const val secretKeyName = "biometric_sample_encryption_key"
    }
}
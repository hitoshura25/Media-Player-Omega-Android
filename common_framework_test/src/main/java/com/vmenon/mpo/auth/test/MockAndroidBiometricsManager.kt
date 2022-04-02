package com.vmenon.mpo.auth.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.auth.framework.biometrics.AndroidBiometricsManager
import com.vmenon.mpo.system.domain.Logger
import javax.crypto.Cipher

class MockAndroidBiometricsManager(
    context: Context,
    logger: Logger
) : AndroidBiometricsManager(context, logger) {

    override val userAuthenticationRequired = false
    var mockedAuthType: Int = BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    override fun canAuthenticate(authType: Int): Int = mockedAuthType
    override fun authenticate(
        activity: AppCompatActivity,
        request: PromptRequest,
        cipher: Cipher,
        processSuccess: (BiometricPrompt.CryptoObject?) -> Unit
    ) {
        processSuccess(BiometricPrompt.CryptoObject(cipher))
    }
}
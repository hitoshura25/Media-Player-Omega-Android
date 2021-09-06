package com.vmenon.mpo.auth.framework.biometrics

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import com.vmenon.mpo.auth.domain.biometrics.BiometricState
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager

class DefaultBiometricsManager(context: Context) : BiometricsManager {
    private val appContext = context.applicationContext

    override fun biometricState(): BiometricState =
        when (BiometricManager.from(appContext).canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricState.SUCCESS
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricState.REQUIRES_ENROLLMENT
            else -> BiometricState.NOT_SUPPORTED
        }
}
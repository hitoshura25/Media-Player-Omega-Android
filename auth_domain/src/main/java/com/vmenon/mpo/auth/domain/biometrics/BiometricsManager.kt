package com.vmenon.mpo.auth.domain.biometrics

import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher

interface BiometricsManager {
    fun biometricState(): BiometricState
    fun <T: Any> showBiometricPrompt(requester: T, request: PromptRequest)
    fun cipher(): Flow<Cipher>

    suspend fun requestBiometricPrompt(reason: PromptReason)
    val biometricPromptRequested: Flow<PromptReason>
}
package com.vmenon.mpo.auth.domain.biometrics

import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher

interface BiometricsManager {
    fun biometricState(): BiometricState
    fun <T : Any> requestBiometricPrompt(requester: T, request: PromptRequest)
    val encryptionCipher: Flow<Cipher>
    val decryptionCipher: Flow<Cipher>
}
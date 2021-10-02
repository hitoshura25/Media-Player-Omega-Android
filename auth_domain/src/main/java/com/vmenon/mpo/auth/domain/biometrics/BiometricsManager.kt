package com.vmenon.mpo.auth.domain.biometrics

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface BiometricsManager {
    fun deviceSupportsBiometrics(): Boolean
    fun enrollmentRequired(): Boolean
    fun <T : Any> requestBiometricPrompt(requester: T, request: PromptRequest)
    val promptResponse: Flow<PromptResponse>
    val promptToEnroll: MutableSharedFlow<PromptRequest>
}
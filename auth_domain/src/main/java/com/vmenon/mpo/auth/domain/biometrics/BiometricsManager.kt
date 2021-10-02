package com.vmenon.mpo.auth.domain.biometrics

import kotlinx.coroutines.flow.Flow

interface BiometricsManager {
    fun canUseBiometrics(): Boolean
    fun <T : Any> requestBiometricPrompt(requester: T, request: PromptRequest)
    val promptResponse: Flow<PromptResponse>
    val enrollmentRequired: Flow<Unit>
}
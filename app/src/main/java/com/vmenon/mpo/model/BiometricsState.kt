package com.vmenon.mpo.model

import com.vmenon.mpo.auth.domain.biometrics.PromptRequest

sealed class BiometricsState {
    data class PromptToEnroll(val request: PromptRequest) : BiometricsState()
    data class PromptAfterEnrollment(val request: PromptRequest): BiometricsState()
    data class PromptToStayAuthenticated(val enrollmentRequired: Boolean = false) : BiometricsState()
}
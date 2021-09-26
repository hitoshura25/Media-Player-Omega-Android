package com.vmenon.mpo.auth.domain.biometrics

data class PromptRequest(
    val reason: PromptReason,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val confirmationRequired: Boolean = true,
    val negativeActionText: String? = null
)
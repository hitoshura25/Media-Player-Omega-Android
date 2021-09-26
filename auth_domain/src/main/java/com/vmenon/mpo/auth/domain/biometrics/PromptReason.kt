package com.vmenon.mpo.auth.domain.biometrics

import com.vmenon.mpo.auth.domain.CipherEncryptedData

sealed class PromptReason {
    object Enrollment : PromptReason()
    data class Login(val cipherEncryptedData: CipherEncryptedData) : PromptReason()
    data class StayAuthenticated(val cipherEncryptedData: CipherEncryptedData) : PromptReason()
}
package com.vmenon.mpo.auth.domain.biometrics

import com.vmenon.mpo.auth.domain.CipherEncryptedData

sealed class PromptReason {
    object Confirmation : PromptReason()
    object Encryption : PromptReason()
    data class Decryption(val cipherEncryptedData: CipherEncryptedData) : PromptReason()
}
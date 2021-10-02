package com.vmenon.mpo.auth.domain.biometrics

import javax.crypto.Cipher

sealed class PromptResponse(open val request: PromptRequest) {
    data class ConfirmationSuccess(override val request: PromptRequest) : PromptResponse(request)
    data class EncryptionSuccess(
        override val request: PromptRequest,
        val encryptionCipher: Cipher
    ) : PromptResponse(
        request
    )

    data class DecryptionSuccess(
        override val request: PromptRequest,
        val decryptionCipher: Cipher
    ) : PromptResponse(
        request
    )
}
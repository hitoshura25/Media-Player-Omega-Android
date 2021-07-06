package com.vmenon.mpo.api.model

import java.io.Serializable

data class RegisterUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)  : Serializable
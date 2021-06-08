package com.vmenon.mpo.api.model

data class RegisterUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)
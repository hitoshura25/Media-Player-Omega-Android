package com.vmenon.mpo.api.model

import java.io.Serializable

data class RegisterUserResponse(
    val userDetails: UserDetails,
    val authenticationProvider: AuthenticationProvider
)  : Serializable
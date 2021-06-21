package com.vmenon.mpo.auth.domain

data class Credentials(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String,
    val accessTokenExpiration: Long,
    val tokenType: String
)

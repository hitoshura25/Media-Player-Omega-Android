package com.vmenon.mpo.login.domain

data class Credentials(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String,
    val accessTokenExpiration: Long,
    val tokenType: String
)

package com.vmenon.mpo.login.domain

interface LoginService {
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User

    suspend fun getUser(): Result<User>

    suspend fun isEnrolledInBiometrics(): Boolean

    suspend fun didUserDeclineBiometricsEnrollment(): Boolean

    suspend fun userDeclinedBiometricsEnrollment()
}
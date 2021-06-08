package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.User

interface UserRegistry {
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String
    ): User

    suspend fun getCurrentUser(): User
}
package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.domain.User

class LoginRepository(
    private val registry: UserRegistry
) : LoginService {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User = registry.registerUser(firstName, lastName, email, password)

    override suspend fun getUser(): User = registry.getCurrentUser()
}
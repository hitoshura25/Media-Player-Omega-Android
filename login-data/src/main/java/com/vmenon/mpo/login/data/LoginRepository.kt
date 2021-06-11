package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.domain.User

class LoginRepository(
    private val registry: UserRegistry,
    private val authState: AuthState
) : LoginService {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User = registry.registerUser(firstName, lastName, email, password)

    override suspend fun getUser(): Result<User> {
        return try {
            Result.success(registry.getCurrentUser())
        } catch (exception: GetUserException) {
            println("Clearing out credentials due to $exception")
            authState.clearCredentials()
            Result.failure(exception)
        }
    }
}
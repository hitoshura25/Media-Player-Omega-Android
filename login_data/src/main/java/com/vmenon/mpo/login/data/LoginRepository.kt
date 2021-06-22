package com.vmenon.mpo.login.data

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.domain.User

class LoginRepository(
    private val registry: UserRegistry,
    private val userCache: UserCache,
    private val system: System
) : LoginService {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User = registry.registerUser(firstName, lastName, email, password)

    override suspend fun getUser(): Result<User> {
        return try {
            val user = userCache.getCachedUser()
            if (user != null) {
                Result.success(user)
            } else {
                val freshUser = registry.getCurrentUser()
                userCache.cacheUser(freshUser)
                Result.success(freshUser)
            }
        } catch (exception: GetUserException) {
            system.println("Clearing out credentials", exception)
            userCache.clear()
            Result.failure(exception)
        }
    }
}
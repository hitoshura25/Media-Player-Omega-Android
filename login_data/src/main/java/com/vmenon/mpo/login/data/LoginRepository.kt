package com.vmenon.mpo.login.data

import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.domain.User
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginRepository(
    private val registry: UserRegistry,
    private val userCache: UserCache,
    private val userSettings: UserSettings,
    private val biometricsManager: BiometricsManager,
    private val logger: Logger
) : LoginService {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            biometricsManager.encryptionCipher.collect {
                userSettings.setEnrolledInBiometrics(true)
            }
        }
    }

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
            logger.println("Clearing out credentials", exception)
            userCache.clear()
            Result.failure(exception)
        }
    }

    override suspend fun isEnrolledInBiometrics(): Boolean = userSettings.enrolledInBiometrics()

    override suspend fun didUserDeclineBiometricsEnrollment(): Boolean =
        userSettings.userDeclinedBiometrics()

    override suspend fun userDeclinedBiometricsEnrollment() {
        userSettings.setUserDeclinedBiometrics(true)
        userSettings.setEnrolledInBiometrics(false)
    }
}
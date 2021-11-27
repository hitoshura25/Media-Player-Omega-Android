package com.vmenon.mpo.login.framework

import com.vmenon.mpo.api.model.RegisterUserRequest
import com.vmenon.mpo.api.model.UserDetails
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.login.data.GetUserException
import com.vmenon.mpo.login.data.UserRegistry
import com.vmenon.mpo.login.domain.User
import retrofit2.HttpException
import java.lang.RuntimeException

class MpoApiUserRegistry(
    private val api: MediaPlayerOmegaRetrofitService
) : UserRegistry {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User {
        val response = api.registerUser(
            RegisterUserRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        ).blockingGet()
        return response.userDetails.run {
            User(
                firstName = firstName,
                lastName = lastName,
                email = email
            )
        }
    }

    override suspend fun getCurrentUser(): User {
        try {
            val userFromApi: UserDetails = api.getCurrentUser().blockingGet()
            return User(
                firstName = userFromApi.firstName,
                lastName = userFromApi.lastName,
                email = userFromApi.email
            )
        } catch (httpException: HttpException) {
            throw GetUserException(httpException)
        } catch (exception: RuntimeException) {
            throw exception.cause ?: exception
        }
    }
}
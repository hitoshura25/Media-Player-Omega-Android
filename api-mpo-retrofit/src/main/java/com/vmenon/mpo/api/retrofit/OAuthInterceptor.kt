package com.vmenon.mpo.api.retrofit

import com.vmenon.mpo.login.domain.AuthService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class OAuthInterceptor(private val authService: AuthService) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = authService.getCredentials()
        val request: Request = chain.request()

        return if (credentials != null) {
            chain.proceed(
                request.newBuilder().addHeader(
                    "Authorization",
                    "${credentials.tokenType} ${credentials.accessToken}"
                ).build()
            )
        } else {
            chain.proceed(request)
        }
    }
}
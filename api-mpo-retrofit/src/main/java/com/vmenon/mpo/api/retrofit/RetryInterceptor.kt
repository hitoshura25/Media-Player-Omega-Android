package com.vmenon.mpo.api.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var currentAttempt = 0
        var response: Response? = null
        var currentDelay = 1000L
        val delayFactor = 2

        while (response == null) {
            try {
                response = chain.proceed(chain.request())
            } catch (timeoutException: SocketTimeoutException) {
                currentAttempt++
                if (currentAttempt < maxRetries) {
                    Thread.sleep(currentDelay)
                    currentDelay = (currentDelay * delayFactor)
                } else {
                    break
                }
            }
        }

        // Let final attempt just fall through
        return response ?: chain.proceed(chain.request())
    }
}
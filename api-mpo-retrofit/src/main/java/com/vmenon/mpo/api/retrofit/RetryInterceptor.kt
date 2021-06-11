package com.vmenon.mpo.api.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var currentAttempt = 0
        var response: Response? = null
        var currentDelay = 3000L
        val delayFactor = 2

        while (response == null) {
            try {
                response = chain.proceed(chain.request())
            } catch (exception: Exception) {
                when (exception) {
                    is SocketTimeoutException, is ConnectException -> {
                        currentAttempt++
                        println("Going to retry with attempt $currentAttempt after delay of $currentDelay")
                        Thread.sleep(currentDelay)

                        if (currentAttempt < maxRetries) {
                            currentDelay = (currentDelay * delayFactor)
                        } else {
                            break
                        }
                    }
                    else -> throw exception
                }
            }
        }

        // Let final attempt just fall through
        return response ?: chain.proceed(chain.request())
    }
}
package com.vmenon.mpo.api.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var currentAttempt = 0
        var response: Response? = null
        var currentDelay = 2000L
        val delayFactor = 2

        while (response?.isSuccessful != true) {
            var doRetry: Boolean
            try {
                response = chain.proceed(chain.request())
                doRetry = !response.isSuccessful
                        && response.code() != HttpURLConnection.HTTP_UNAUTHORIZED
            } catch (exception: Exception) {
                doRetry = shouldDoRetry(exception)
            }

            if (doRetry) {
                currentAttempt++
                println("Going to retry with attempt $currentAttempt after delay of $currentDelay")
                Thread.sleep(currentDelay)

                if (currentAttempt < maxRetries) {
                    currentDelay = (currentDelay * delayFactor)
                } else {
                    break
                }
            }
        }

        // Let final attempt just fall through
        return response ?: chain.proceed(chain.request())
    }

    private fun shouldDoRetry(exception: Exception): Boolean {
        return when (exception) {
            is SocketTimeoutException, is ConnectException -> true
            is HttpException -> (exception.code() != HttpURLConnection.HTTP_UNAUTHORIZED)
            else -> true
        }
    }
}
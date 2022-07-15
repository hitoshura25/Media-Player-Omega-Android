package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.system.domain.ThreadUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

class RetryInterceptorTest {
    private val logger: Logger = mock()
    private val threadUtil: ThreadUtil = mock()

    private val response: Response = mock(name = "response")
    private val requestBuilder: Request.Builder = mock()
    private val request: Request = mock(name = "interceptedRequest") {
        on { newBuilder() }.thenReturn(requestBuilder)
    }

    private val chain: Interceptor.Chain = mock {
        on { proceed(request) }.thenReturn(response)
        on { request() }.thenReturn(request)
    }

    private val retryInterceptor = RetryInterceptor(2, logger, threadUtil)

    @Test
    fun givenResponseSuccessOnFirstWhenInterceptedThenNoRetry() {
        whenever(response.isSuccessful).thenReturn(true)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(1)).proceed(request)
    }

    @Test
    fun givenResponseNotSuccessfulButIsHttpUnauthorizedWhenInterceptedThenNoRetry() {
        whenever(response.isSuccessful).thenReturn(false)
        whenever(response.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)

        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(1)).proceed(request)
    }

    @Test
    fun givenResponseNotSuccessfulThenSuccessfulWhenInterceptedThenRetryOnce() {
        whenever(response.isSuccessful).thenReturn(false).thenReturn(false).thenReturn(true)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(2)).proceed(request)
    }

    @Test
    fun givenResponseNeverSuccessfulWhenInterceptedThenOnlyRetryTwice() {
        whenever(response.isSuccessful).thenReturn(false)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(3)).proceed(request)
    }

    @Test
    fun givenRequestThrowsSocketExceptionThenSuccessfulWhenInterceptedThenRetryOnce() {
        whenever(chain.proceed(request)).thenThrow(SocketTimeoutException()).thenReturn(response)
        whenever(response.isSuccessful).thenReturn(true)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(2)).proceed(request)
    }

    @Test
    fun givenRequestThrowsConnectExceptionThenSuccessfulWhenInterceptedThenRetryOnce() {
        whenever(chain.proceed(request)).thenThrow(ConnectException()).thenReturn(response)
        whenever(response.isSuccessful).thenReturn(true)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(2)).proceed(request)
    }

    @Test
    fun givenRequestThrowsHttpUnauthorizedExceptionThenSuccessfulWhenInterceptedThenReturnResponse() {
        val httpException: HttpException = mock {
            on { code() }.thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
        }
        whenever(chain.proceed(request)).thenThrow(httpException).thenReturn(response)
        assertEquals(response, retryInterceptor.intercept(chain))
        verify(chain, times(2)).proceed(request)
    }

    @Test
    fun givenRequestThrowsHttpUnauthorizedExceptionAlwaysWhenInterceptedThenThrow() {
        val httpException: HttpException = mock {
            on { code() }.thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
        }
        whenever(chain.proceed(request)).thenThrow(httpException)
        assertThrows(HttpException::class.java) { retryInterceptor.intercept(chain) }
        verify(chain, times(2)).proceed(request)
    }
}
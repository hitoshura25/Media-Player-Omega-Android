package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.RefreshCredentialsCallback
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.common.framework.retrofit.OAuthInterceptor.ResponseWithCredentials
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
class OAuthInterceptorTest {
    private val authService: AuthService = mock()
    private val logger: Logger = mock()
    private val timeOfFirstRunWithFresh = 1000L
    private val timeOfSecondRunWithFresh = 2000L
    private val clock: Clock = mock {
        on { currentTimeMillis() }
            .thenReturn(timeOfFirstRunWithFresh)
            .thenReturn(timeOfSecondRunWithFresh)
    }

    private val interceptedRequestBuilder: Request.Builder = mock()
    private val interceptedRequest: Request = mock(name = "interceptedRequest") {
        on { newBuilder() }.thenReturn(interceptedRequestBuilder)
    }

    private val requestWithFreshCredentials: Request = mock(name = "requestWithFreshCredentials")
    private val requestWithCurrentCredentialsBuilder: Request.Builder = mock {
        on { build() }.thenReturn(requestWithFreshCredentials)
    }

    private val requestWithCurrentCredentials: Request =
        mock(name = "requestWithCurrentCredentials") {
            on { newBuilder() }.thenReturn(requestWithCurrentCredentialsBuilder)
        }

    private val response: Response = mock(name = "response")
    private val responseFromFreshCredentials: Response = mock(name = "responseFromFreshCredentials")
    private val responseFromCurrentCredentials: Response =
        mock(name = "responseFromCurrentCredentials")

    private val chain: Interceptor.Chain = mock {
        on { proceed(interceptedRequest) }.thenReturn(response)
        on { proceed(requestWithCurrentCredentials) }.thenReturn(responseFromCurrentCredentials)
        on { proceed(requestWithFreshCredentials) }.thenReturn(responseFromFreshCredentials)
        on { request() }.thenReturn(interceptedRequest)
    }
    private val credentials = Credentials(
        accessToken = "accessToken",
        refreshToken = "refreshToken",
        idToken = "idToken",
        accessTokenExpiration = 100L,
        tokenType = "bearer"
    )
    private val refreshedCredentials = Credentials(
        accessToken = "accessToken2",
        refreshToken = "refreshToken2",
        idToken = "idToken",
        accessTokenExpiration = 100L,
        tokenType = "bearer"
    )

    private val interceptor = OAuthInterceptor(authService, logger, clock)

    @Before
    fun setup() {
        whenever(interceptedRequestBuilder.addHeader(any(), any())).thenReturn(
            interceptedRequestBuilder
        )
        whenever(requestWithCurrentCredentialsBuilder.addHeader(any(), any())).thenReturn(
            requestWithCurrentCredentialsBuilder
        )
    }

    @Test
    fun givenNoCredentialsWhenInterceptedThenJustProceed() = runBlockingTest {
        whenever(authService.getCredentials()).thenReturn(CredentialsResult.None)
        assertEquals(response, runCatching { interceptor.intercept(chain) }.getOrThrow())
        verify(authService, times(0)).runWithFreshCredentialsIfNecessary(any(), any())
    }

    @Test
    fun givenHasCredentialsAndTokenRefreshedBeforeRequestAndResponseAuthorizedWhenInterceptedThenAddHeaderWithFreshCredentials() =
        runBlockingTest {
            givenTokenRefreshedBeforeRequest(true)
            assertEquals(
                responseFromFreshCredentials,
                runCatching { interceptor.intercept(chain) }.getOrThrow()
            )
            verify(interceptedRequestBuilder).addHeader(
                "Authorization",
                "bearer ${refreshedCredentials.accessToken}"
            )
        }

    @Test
    fun givenHasCredentialsAndTokenRefreshedBeforeRequestAndResponseNotAuthorizedWhenInterceptedThenAddHeaderWithFreshCredentials() =
        runBlockingTest {
            givenTokenRefreshedBeforeRequest(true)
            whenever(responseFromFreshCredentials.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
            assertEquals(
                responseFromFreshCredentials,
                runCatching { interceptor.intercept(chain) }.getOrThrow()
            )
            verify(interceptedRequestBuilder).addHeader(
                "Authorization",
                "bearer ${refreshedCredentials.accessToken}"
            )
        }

    @Test
    fun givenHasCredentialsAndTokenNotNotRefreshedBeforeRequestAndResponseAuthorizedWhenInterceptedThenAddHeaderWithCurrentCredentials() =
        runBlockingTest {
            givenTokenRefreshedBeforeRequest(false)
            assertEquals(
                responseFromCurrentCredentials,
                runCatching { interceptor.intercept(chain) }.getOrThrow()
            )
            verify(interceptedRequestBuilder).addHeader(
                "Authorization",
                "bearer ${credentials.accessToken}"
            )
        }

    @Test
    fun givenHasCredentialsAndTokenNotNotRefreshedBeforeRequestAndResponseNotAuthorizedWhenInterceptedThenAddHeaderWithFreshCredentials() =
        runBlockingTest {
            givenTokenRefreshedBeforeRequest(false)
            whenever(responseFromCurrentCredentials.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
            assertEquals(
                responseFromFreshCredentials,
                runCatching { interceptor.intercept(chain) }.getOrThrow()
            )
            verify(requestWithCurrentCredentialsBuilder).addHeader(
                "Authorization",
                "bearer ${refreshedCredentials.accessToken}"
            )
        }

    @Test
    fun givenHasCredentialsAndTokenNotNotRefreshedBeforeRequestAndResponseStillNotAuthorizedWhenInterceptedThenAddHeaderWithFreshCredentials() =
        runBlockingTest {
            givenTokenRefreshedBeforeRequest(false)
            whenever(responseFromCurrentCredentials.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
            whenever(responseFromFreshCredentials.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED)
            assertEquals(
                responseFromFreshCredentials,
                runCatching { interceptor.intercept(chain) }.getOrThrow()
            )
            verify(requestWithCurrentCredentialsBuilder).addHeader(
                "Authorization",
                "bearer ${refreshedCredentials.accessToken}"
            )
        }

    private suspend fun givenTokenRefreshedBeforeRequest(refreshed: Boolean) {
        whenever(
            authService.runWithFreshCredentialsIfNecessary<ResponseWithCredentials>(any(), any())
        ).doSuspendableAnswer { invocation ->
            val comparisonTime = invocation.getArgument<Long>(0)
            val operation: RefreshCredentialsCallback<ResponseWithCredentials> =
                invocation.getArgument(1)
            operation(
                Result.success(
                    if (comparisonTime == timeOfFirstRunWithFresh) refreshed else true
                )
            )
        }

        whenever(authService.getCredentials()).thenReturn(
            CredentialsResult.Success(credentials)
        ).thenReturn(
            CredentialsResult.Success(if (refreshed) refreshedCredentials else credentials)
        ).thenReturn(
            CredentialsResult.Success(refreshedCredentials)
        )

        whenever(interceptedRequestBuilder.build()).thenReturn(
            if (refreshed) requestWithFreshCredentials else requestWithCurrentCredentials
        )
    }
}
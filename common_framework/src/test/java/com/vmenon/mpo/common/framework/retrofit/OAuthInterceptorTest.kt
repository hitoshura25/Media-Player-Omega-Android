package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.RefreshCredentialsCallback
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class OAuthInterceptorTest {
    private val authService: AuthService = mock()
    private val logger: Logger = mock()
    private val clock: Clock = mock()
    private val newRequest: Request = mock()
    private val requestBuilder: Request.Builder = mock {
        on { build() }.thenReturn(newRequest)
    }
    private val request: Request = mock {
        on { newBuilder() }.thenReturn(requestBuilder)
    }
    private val response: Response = mock()
    private val newResponse: Response = mock()
    private val chain: Interceptor.Chain = mock {
        on { proceed(request) }.thenReturn(response)
        on { proceed(newRequest) }.thenReturn(newResponse)
        on { request() }.thenReturn(request)
    }

    private val interceptor = OAuthInterceptor(authService, logger, clock)

    @Before
    fun setup() {
        whenever(requestBuilder.addHeader(any(), any())).thenReturn(requestBuilder)
    }

    @Test
    fun givenNoCredentialsWhenInterceptedThenJustProceed() = runBlockingTest {
        whenever(authService.getCredentials()).thenReturn(CredentialsResult.None)
        runCatching { interceptor.intercept(chain) }
        verify(authService, times(0)).runWithFreshCredentialsIfNecessary(any(), any())
    }

    @Test
    fun givenHasCredentialsAndAuthServiceRefreshedWhenInterceptedThenRunWithFreshCredentials() =
        runBlockingTest {
            val credentials = Credentials(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                idToken = "idToken",
                accessTokenExpiration = 100L,
                tokenType = "oauth"
            )
            val refreshedCredentials = Credentials(
                accessToken = "accessToken",
                refreshToken = "refreshToken2",
                idToken = "idToken",
                accessTokenExpiration = 100L,
                tokenType = "oauth"
            )
            whenever(clock.currentTimeMillis()).thenReturn(1000L)
            whenever(authService.getCredentials()).thenReturn(
                CredentialsResult.Success(credentials)
            ).thenReturn(
                CredentialsResult.Success(refreshedCredentials)
            )
            whenever(
                authService.runWithFreshCredentialsIfNecessary<OAuthInterceptor.ResponseWithCredentials>(
                    eq(1000L),
                    any()
                )
            ).doSuspendableAnswer { invocation ->
                val operation: RefreshCredentialsCallback<OAuthInterceptor.ResponseWithCredentials> =
                    invocation.getArgument(1)
                operation(Result.success(true))
            }
            assertEquals(newResponse, runCatching { interceptor.intercept(chain) }.getOrThrow())
        }
}
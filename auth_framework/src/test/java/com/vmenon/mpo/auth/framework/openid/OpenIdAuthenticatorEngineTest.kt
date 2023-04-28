package com.vmenon.mpo.auth.framework.openid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.auth.domain.AuthException
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.TokenResponse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class OpenIdAuthenticatorEngineTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val logger: Logger = mock()
    private val authorizationService: AuthorizationService = mock()

    private val openIdAuthenticatorEngine = OpenIdAuthenticatorEngine(
        authorizationService = authorizationService,
        logger = logger,
        scope = ""
    ).apply {
        serviceConfiguration = mock()
    }

    @Test(expected = IllegalStateException::class)
    fun whenHandleAuthResponseCalledWithAuthorizationResponseAndExceptionNullThenThrowIllegalStateException() =
        runTest {
            openIdAuthenticatorEngine.handleAuthResponse(null, null)
        }

    @Test(expected = AuthorizationException::class)
    fun whenHandleAuthResponseCalledWithAuthorizationResponseNullAndExceptionNotNullThenThrowException() =
        runTest {
            openIdAuthenticatorEngine.handleAuthResponse(
                null,
                AuthorizationException(
                    0,
                    0,
                    null,
                    null,
                    null,
                    null
                )
            )
        }

    @Test
    fun whenHandleEndSessionResponseCalledWithNullExceptionThenNothingHappens() {
        openIdAuthenticatorEngine.handleEndSessionResponse(null)
        verifyNoInteractions(logger)
    }

    @Test
    fun whenHandleEndSessionResponseCalledWithNotNullExceptionThenCallLogger() {
        val exception = AuthorizationException(
            0,
            0,
            null,
            null,
            null,
            null
        )
        openIdAuthenticatorEngine.handleEndSessionResponse(exception)
        verify(logger).println(any(), eq(exception))
    }

    @Test
    fun givenNullExceptionAndResponseGivenToRefreshTokenCallbackWhenRefreshTokenCalledThenResultFailsWithIllegalArgumentException() = runTest {
        whenever(authorizationService.performTokenRequest(any(), any(), any()))
            .thenAnswer {
                (it.arguments[2] as TokenResponseCallback).onTokenRequestCompleted(
                    null,
                    null,
                )
            }
        val result = openIdAuthenticatorEngine.refreshToken("token")
        assertTrue(result.exceptionOrNull()!! is IllegalArgumentException)
    }

    @Test
    fun givenExceptionAndNullResponseGivenToRefreshTokenCallbackWhenRefreshTokenCalledThenResultFailsWithAuthException() = runTest {
        val exception = AuthorizationException(
            0,
            0,
            null,
            null,
            null,
            null
        )
        whenever(authorizationService.performTokenRequest(any(), any(), any()))
            .thenAnswer {
                (it.arguments[2] as TokenResponseCallback).onTokenRequestCompleted(
                    null,
                    exception,
                )
            }
        val result = openIdAuthenticatorEngine.refreshToken("token")
        assertTrue(result.exceptionOrNull()!! is AuthException)
    }

    @Test
    fun givenExceptionIsNullAndResponseWithNullFieldsGivenToRefreshTokenCallbackWhenRefreshTokenCalledThenResultFailsWithAuthException() = runTest {
        val response: TokenResponse = mock()
        whenever(authorizationService.performTokenRequest(any(), any(), any()))
            .thenAnswer {
                (it.arguments[2] as TokenResponseCallback).onTokenRequestCompleted(
                    response,
                    null,
                )
            }
        val result = openIdAuthenticatorEngine.refreshToken("token")
        assertTrue(result.exceptionOrNull()!! is IllegalArgumentException)
    }
}
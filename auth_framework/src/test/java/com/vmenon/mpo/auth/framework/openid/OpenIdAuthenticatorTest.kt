package com.vmenon.mpo.auth.framework.openid

import android.content.Context
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class OpenIdAuthenticatorTest {
    private val appContext: Context = mock()
    private val authState: AuthState = mock()
    private val logger: Logger = mock()
    private val authenticatorEngine: OpenIdAuthenticatorEngine = mock()

    private val authenticator = OpenIdAuthenticator(
        appContext,
        authState,
        logger,
        authenticatorEngine,
    )

    @Test(expected = IllegalStateException::class)
    fun whenStartAuthenticationCalledWithNotActivityContextThenThrowIllegalStateException() {
        authenticator.startAuthentication(mock())
    }

    @Test(expected = IllegalStateException::class)
    fun whenLogoutCalledWithNotActivityContextThenThrowIllegalStateException() {
        authenticator.logout(mock())
    }

    @Test
    fun givenAuthenticatorEngineRefreshTokenSuccessWhenRefreshTokenCalledThenReturnTrue() =
        runTest {
            val result = Result.success(
                Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                    idToken = "idToken",
                    accessTokenExpiration = 60100L,
                    tokenType = "tokenType"
                )
            )
            whenever(authenticatorEngine.refreshToken("refreshToken")).thenReturn(result)
            assertTrue(authenticator.refreshToken("refreshToken").getOrThrow())
        }

    @Test
    fun givenAuthenticatorEngineRefreshTokenThrowsExceptionWhenRefreshTokenCalledThenReturnException() =
        runTest {
            val exception = Exception()
            whenever(authenticatorEngine.refreshToken("refreshToken")).thenReturn(
                Result.failure(
                    exception
                )
            )
            assertEquals(exception, authenticator.refreshToken("refreshToken").exceptionOrNull())
        }
}
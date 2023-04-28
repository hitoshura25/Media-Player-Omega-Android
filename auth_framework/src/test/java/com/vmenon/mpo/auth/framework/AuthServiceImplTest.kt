package com.vmenon.mpo.auth.framework

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthServiceImplTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authState: AuthState = mock()
    private val authenticator: Authenticator = mock()
    private val biometricsManager: BiometricsManager = mock {
        on { promptResponse } doReturn MutableSharedFlow()
    }

    private val authService = AuthServiceImpl(
        authState = authState,
        authenticator = authenticator,
        biometricsManager = biometricsManager,
    )

    @Test
    fun givenNoCredentialsWhenRunWithFreshCredentialsIfNecessaryCalledThenOperationCalledWithFalse() =
        runTest {
            whenever(authState.getCredentials()).thenReturn(CredentialsResult.None)
            authService.runWithFreshCredentialsIfNecessary(100) {
                assertFalse(it.getOrThrow())
            }
        }

    @Test
    fun givenRequiresBiometricAuthWhenRunWithFreshCredentialsIfNecessaryCalledThenOperationCalledWithFalse() =
        runTest {
            whenever(authState.getCredentials()).thenReturn(
                CredentialsResult.RequiresBiometricAuth(
                    mock()
                )
            )
            authService.runWithFreshCredentialsIfNecessary(100) {
                assertFalse(it.getOrThrow())
            }
        }

    @Test
    fun givenCredentialsAndNotExpiredWhenRunWithFreshCredentialsIfNecessaryCalledThenOperationCalledWithFalse() =
        runTest {
            whenever(authState.getCredentials()).thenReturn(
                CredentialsResult.Success(
                    Credentials(
                        accessToken = "accessToken",
                        refreshToken = "refreshToken",
                        idToken = "idToken",
                        accessTokenExpiration = 60100L,
                        tokenType = "tokenType"
                    )
                )
            )
            authService.runWithFreshCredentialsIfNecessary(100) {
                assertFalse(it.getOrThrow())
            }
        }

    @Test
    fun givenCredentialsAndExpiredWhenRunWithFreshCredentialsIfNecessaryCalledThenOperationCalledWithRefreshTokenResult() =
        runTest {
            whenever(authState.getCredentials()).thenReturn(
                CredentialsResult.Success(
                    Credentials(
                        accessToken = "accessToken",
                        refreshToken = "refreshToken",
                        idToken = "idToken",
                        accessTokenExpiration = 60000L,
                        tokenType = "tokenType"
                    )
                )
            )
            val refreshTokenResult = Result.success(true)
            whenever(authenticator.refreshToken("refreshToken")).thenReturn(refreshTokenResult)
            authService.runWithFreshCredentialsIfNecessary(100) {
                assertEquals(refreshTokenResult, it)
            }
        }
}
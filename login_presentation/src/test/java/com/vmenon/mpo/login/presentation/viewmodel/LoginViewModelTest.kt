package com.vmenon.mpo.login.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import com.vmenon.mpo.assertValues
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.getOrAwaitValue
import com.vmenon.mpo.login.domain.User
import com.vmenon.mpo.login.presentation.R
import com.vmenon.mpo.login.presentation.model.LoadingState
import com.vmenon.mpo.login.presentation.model.LoggedInState
import com.vmenon.mpo.login.presentation.model.LoginState
import com.vmenon.mpo.test
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.Exception

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val fragment: Fragment = mock {
        on { getString(R.string.login) } doReturn ""
    }

    private val viewModel = LoginViewModel().apply {
        loginService = mock()
        authService = mock()
        buildConfigProvider = mock {
            on { appVersion() } doReturn "1.0"
            on { buildNumber() } doReturn "1"
        }
        biometricsManager = mock()
        dispatcher = testCoroutineRule.testCoroutineDispatcher
    }

    @Test
    fun givenCredentialsRequiresBiometricAuthWhenLoginWithBiometricsThenPrompt() = runTest {
        whenever(viewModel.authService.getCredentials()).thenReturn(
            CredentialsResult.RequiresBiometricAuth(
                mock()
            )
        )

        viewModel.loginWithBiometrics(fragment)
    }

    @Test
    fun givenCredentialsNotRequiresBiometricAuthWhenLoginWithBiometricsThenPrompt() = runTest {
        whenever(viewModel.authService.getCredentials()).thenReturn(
            CredentialsResult.None
        )

        viewModel.loginWithBiometrics(fragment)
    }

    @Test
    fun givenCredentialsAndGetUserErrorsAndIsNotEnrolledInBiometricsAndDevicesDoesNotSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.failure(Exception()))
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            val testObserver = viewModel.loginState().test()
            testObserver.assertValues(
                {
                    assertEquals(LoadingState, it.anyContent())
                },
                {
                    assertEquals(
                        LoginState(
                            canUseBiometrics = false,
                            version = "1.0",
                            buildNumber = "1",
                        ),
                        it.anyContent()
                    )
                }
            )
        }

    @Test
    fun givenCredentialsAndGetUserErrorsAndIsEnrolledInBiometricsAndDevicesDoesNotSupportsBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.failure(Exception()))
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(true)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            val loginState = viewModel.loginState()
            assertTrue(loginState.getOrAwaitValue().anyContent() is LoadingState)
            assertEquals(
                LoginState(
                    canUseBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                ),
                loginState.getOrAwaitValue().anyContent()
            )
        }

    @Test
    fun givenCredentialsAndGetUserErrorsAndIsNotEnrolledInBiometricsAndDevicesDoesSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.failure(Exception()))
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(true)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoginState(
                    canUseBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserErrorsAndIsEnrolledInBiometricsAndDevicesDoesSupportsBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.failure(Exception()))
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(true)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(true)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoginState(
                    canUseBiometrics = true,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasAskedToEnrollAndDeclinedAndIsEnrolledAndDeviceSupportsBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(true)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(true)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(true)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(true)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoggedInState(
                    userDetails = user,
                    promptToEnrollInBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasAskedToEnrollAndDeclinedAndIsEnrolledAndDeviceDoesNotSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(true)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(true)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(true)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoggedInState(
                    userDetails = user,
                    promptToEnrollInBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasAskedToEnrollAndDeclinedAndIsNotEnrolledAndDeviceDoesNotSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(true)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(true)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoggedInState(
                    userDetails = user,
                    promptToEnrollInBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasAskedToEnrollAndNotDeclinedAndIsNotEnrolledAndDeviceDoesNotSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(true)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(false)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            viewModel.loginState().test().assertValues(
                LoadingState,
                LoggedInState(
                    userDetails = user,
                    promptToEnrollInBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasNotAskedToEnrollAndNotDeclinedAndIsNotEnrolledAndDeviceDoesNotSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(false)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(false)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(false)

            val observer = viewModel.loginState().test()

            observer.assertValues(
                LoadingState,
                LoggedInState(
                    userDetails = user,
                    promptToEnrollInBiometrics = false,
                    version = "1.0",
                    buildNumber = "1",
                )
            )
        }

    @Test
    fun givenCredentialsAndGetUserSuccessAndHasNotAskedToEnrollAndNotDeclinedAndIsNotEnrolledAndDeviceDoesSupportBiometricsWhenObservedThenLoginStateEmitsValue() =
        runTest {
            val user: User = mock()
            val credentialsFlow = MutableStateFlow(CredentialsResult.Success(mock()))
            whenever(viewModel.authService.credentials()).thenReturn(credentialsFlow)
            whenever(viewModel.loginService.getUser()).thenReturn(Result.success(user))
            whenever(viewModel.loginService.hasAskedToEnrollInBiometrics()).thenReturn(false)
            whenever(viewModel.loginService.didUserDeclineBiometricsEnrollment()).thenReturn(false)
            whenever(viewModel.loginService.isEnrolledInBiometrics()).thenReturn(false)
            whenever(viewModel.biometricsManager.deviceSupportsBiometrics()).thenReturn(true)

            val observer = viewModel.loginState().test()

            observer.assertValues(
                {
                    assertEquals(LoadingState, it.anyContent())
                },
                {
                    assertEquals(
                        LoggedInState(
                            userDetails = user,
                            promptToEnrollInBiometrics = true,
                            version = "1.0",
                            buildNumber = "1",
                        ),
                        it.anyContent()
                    )
                }
            )
        }
}
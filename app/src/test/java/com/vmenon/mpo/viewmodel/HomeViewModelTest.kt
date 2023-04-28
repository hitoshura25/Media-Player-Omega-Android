package com.vmenon.mpo.viewmodel

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val viewModel = HomeViewModel()

    @Before
    fun setup() {
        viewModel.biometricsManager = mock()
        viewModel.authService = mock()
        viewModel.buildConfigProvider = mock()
    }

    @Test
    fun promptForBiometricsAfterEnrollmentCallsBiometricManager() {
        testCoroutineRule.runBlockingTest {
            val request = mock<PromptRequest>()
            val activity = mock<AppCompatActivity>()
            viewModel.promptForBiometricsAfterEnrollment(activity, request)
            verify(viewModel.biometricsManager).requestBiometricPrompt(activity, request)
        }
    }

    @Test
    fun promptForBiometricEnrollmentOnSDKVersionR() {
        whenever(viewModel.buildConfigProvider.sdkVersion()).thenReturn(Build.VERSION_CODES.R)
        viewModel.biometricEnrollmentLauncher = mock()
        viewModel.promptForBiometricEnrollment(mock())
        verify(viewModel.biometricEnrollmentLauncher!!).launch(any())
    }

    @Test
    fun promptForBiometricEnrollmentOnSDKVersionP() {
        whenever(viewModel.buildConfigProvider.sdkVersion()).thenReturn(Build.VERSION_CODES.P)
        viewModel.biometricEnrollmentLauncher = mock()
        viewModel.promptForBiometricEnrollment(mock())
        verify(viewModel.biometricEnrollmentLauncher!!).launch(any())
    }

    @Test
    fun promptForBiometricEnrollmentOnSDKVersionO() {
        whenever(viewModel.buildConfigProvider.sdkVersion()).thenReturn(Build.VERSION_CODES.O)
        viewModel.biometricEnrollmentLauncher = mock()
        viewModel.promptForBiometricEnrollment(mock())
        verify(viewModel.biometricEnrollmentLauncher!!).launch(any())
    }

    @Test
    fun promptForBiometricEnrollmentOnSDKVersionOAndBiometricEnrollmentLauncherNull() {
        viewModel.biometricEnrollmentLauncher = null
        viewModel.promptForBiometricEnrollment(mock())
    }

    @Test
    fun promptForBiometricsToStayAuthenticatedDoesNothingIfCredentialsNotRequiresBiometricAuth() =
        runTest {
            whenever(viewModel.authService.getCredentials()).thenReturn(CredentialsResult.None)
            viewModel.promptForBiometricsToStayAuthenticated(mock())
            verifyNoInteractions(viewModel.biometricsManager)
        }
}
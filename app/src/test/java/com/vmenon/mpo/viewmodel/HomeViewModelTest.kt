package com.vmenon.mpo.viewmodel

import androidx.appcompat.app.AppCompatActivity
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val viewModel = HomeViewModel()

    @Before
    fun setup() {
        viewModel.biometricsManager = mock()
        viewModel.authService = mock()
        viewModel.navigationController = mock()
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
}
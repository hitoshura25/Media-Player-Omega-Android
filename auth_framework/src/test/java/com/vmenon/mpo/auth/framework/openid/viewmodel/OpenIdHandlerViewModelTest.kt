package com.vmenon.mpo.auth.framework.openid.viewmodel

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import com.vmenon.mpo.auth.framework.openid.OpenIdAuthenticator
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment.Companion.Operation.LOGOUT
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment.Companion.Operation.PERFORM_AUTH
import com.vmenon.mpo.getOrAwaitValue
import com.vmenon.mpo.system.domain.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.vmenon.mpo.noValueExpected
import com.vmenon.mpo.test
import com.vmenon.mpo.test.TestCoroutineRule
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.mockito.kotlin.any
import org.mockito.kotlin.verifyNoInteractions

@ExperimentalCoroutinesApi
class OpenIdHandlerViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val logger: Logger = mock()
    private val authenticator: OpenIdAuthenticator = mock()

    private val viewModel = OpenIdHandlerViewModel().apply {
        logger = this@OpenIdHandlerViewModelTest.logger
        authenticator = this@OpenIdHandlerViewModelTest.authenticator
        dispatcher = testCoroutineRule.testCoroutineDispatcher
    }

    @Test(expected = IllegalStateException::class)
    fun givenActivityResultContractNullWhenOnResumeCalledWithPerformAuthOperationThenThrowException() {
        val bundle: Bundle = mock()
        val fragment: Fragment = mock()
        whenever(bundle.getSerializable(OpenIdHandlerFragment.EXTRA_OPERATION)).thenReturn(
            PERFORM_AUTH
        )
        whenever(fragment.arguments).thenReturn(bundle)

        viewModel.onResume(fragment)
    }

    @Test
    fun givenActivityResultContractNotNullWhenOnResumeCalledWithPerformAuthOperationThenPerformAuth() =
        runTest {
            viewModel.startAuthContract = mock()
            val bundle: Bundle = mock()
            val fragment: Fragment = mock()
            whenever(bundle.getSerializable(OpenIdHandlerFragment.EXTRA_OPERATION)).thenReturn(
                PERFORM_AUTH
            )
            whenever(fragment.arguments).thenReturn(bundle)

            viewModel.onResume(fragment)
            verify(authenticator).performAuthenticate(viewModel.startAuthContract!!)
        }

    @Test(expected = IllegalStateException::class)
    fun givenActivityResultContractNullWhenOnResumeCalledWithLogOutOperationThenThrowException() {
        val bundle: Bundle = mock()
        val fragment: Fragment = mock()
        whenever(bundle.getSerializable(OpenIdHandlerFragment.EXTRA_OPERATION)).thenReturn(
            LOGOUT
        )
        whenever(fragment.arguments).thenReturn(bundle)

        viewModel.onResume(fragment)
    }

    @Test
    fun givenActivityResultContractNotNullAndAuthenticatorLogOutReturnsTreWhenOnResumeCalledWithLogOutOperationThenNoAuthenticatedEmit() =
        runTest {
            val bundle: Bundle = mock()
            val fragment: Fragment = mock()
            viewModel.logoutContract = mock()

            whenever(bundle.getSerializable(OpenIdHandlerFragment.EXTRA_OPERATION)).thenReturn(
                LOGOUT
            )
            whenever(fragment.arguments).thenReturn(bundle)
            whenever(authenticator.performLogoutIfNecessary(viewModel.logoutContract!!)).thenReturn(
                true
            )
            viewModel.onResume(fragment)
            viewModel.authenticated().noValueExpected()
        }

    @Test
    fun givenActivityResultContractNotNullAndAuthenticatorLogOutReturnsFalseWhenOnResumeCalledWithLogOutOperationThenNoAuthenticatedEmit() =
        runTest {
            val bundle: Bundle = mock()
            val fragment: Fragment = mock()
            viewModel.logoutContract = mock()

            whenever(bundle.getSerializable(OpenIdHandlerFragment.EXTRA_OPERATION)).thenReturn(
                LOGOUT
            )
            whenever(fragment.arguments).thenReturn(bundle)
            whenever(authenticator.performLogoutIfNecessary(viewModel.logoutContract!!)).thenReturn(
                false
            )
            viewModel.onResume(fragment)
            assertFalse(viewModel.authenticated().getOrAwaitValue())
        }

    @Test
    fun givenNoOperationArgumentsWhenOnResumeCalledThenDoNothing() =
        runTest {
            val bundle: Bundle = mock()
            val fragment: Fragment = mock()
            whenever(fragment.arguments).thenReturn(bundle)

            viewModel.onResume(fragment)
            verifyNoInteractions(authenticator)
        }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenPerformAuthResultAndLogOutResultNotOkThenAuthenticatedEmitsCorrectValues() {
        val fragment: Fragment = mock()
        val contract: ActivityResultLauncher<StartActivityForResult> = mock()
        whenever(
            fragment.registerForActivityResult(
                any<StartActivityForResult>(),
                any<ActivityResultCallback<ActivityResult>>()
            )
        ).thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }.thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }

        val observer = viewModel.authenticated().test()
        viewModel.onCreated(fragment)
        observer.assertValues(false, false)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenPerformAuthResultOkButNoDataAndLogOutResultNotOkThenAuthenticatedEmitsCorrectValues() {
        val fragment: Fragment = mock()
        val contract: ActivityResultLauncher<StartActivityForResult> = mock()
        whenever(
            fragment.registerForActivityResult(
                any<StartActivityForResult>(),
                any<ActivityResultCallback<ActivityResult>>()
            )
        ).thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_OK,
                    null,
                )
            )
            contract
        }.thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }

        val observer = viewModel.authenticated().test()
        viewModel.onCreated(fragment)
        observer.assertValues(false, false)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenPerformAuthResultOkAndHasDataAndLogOutResultNotOkThenAuthenticatedEmitsCorrectValues() {
        val fragment: Fragment = mock()
        val contract: ActivityResultLauncher<StartActivityForResult> = mock()
        whenever(
            fragment.registerForActivityResult(
                any<StartActivityForResult>(),
                any<ActivityResultCallback<ActivityResult>>()
            )
        ).thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_OK,
                    mock(),
                )
            )
            contract
        }.thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }

        val observer = viewModel.authenticated().test()
        viewModel.onCreated(fragment)
        observer.assertValues(true, false)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenPerformAuthResultNotOkAndLogOutResultOkButNoDataThenAuthenticatedEmitsCorrectValues() {
        val fragment: Fragment = mock()
        val contract: ActivityResultLauncher<StartActivityForResult> = mock()
        whenever(
            fragment.registerForActivityResult(
                any<StartActivityForResult>(),
                any<ActivityResultCallback<ActivityResult>>()
            )
        ).thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }.thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_OK,
                    null,
                )
            )
            contract
        }

        val observer = viewModel.authenticated().test()
        viewModel.onCreated(fragment)
        observer.assertValues(false, false)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenPerformAuthResultNotOkAndLogOutResultOkAndHasDataThenAuthenticatedEmitsCorrectValues() {
        val fragment: Fragment = mock()
        val contract: ActivityResultLauncher<StartActivityForResult> = mock()
        whenever(
            fragment.registerForActivityResult(
                any<StartActivityForResult>(),
                any<ActivityResultCallback<ActivityResult>>()
            )
        ).thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_CANCELED,
                    null,
                )
            )
            contract
        }.thenAnswer {
            (it.arguments[1] as ActivityResultCallback<ActivityResult>).onActivityResult(
                ActivityResult(
                    Activity.RESULT_OK,
                    mock(),
                )
            )
            contract
        }

        val observer = viewModel.authenticated().test()
        viewModel.onCreated(fragment)
        observer.assertValues(false, false)
    }
}
package com.vmenon.mpo.login.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.login.presentation.model.RegistrationObservable
import com.vmenon.mpo.login.presentation.model.RegistrationValid
import com.vmenon.mpo.system.domain.PatternMatcher
import com.vmenon.mpo.test
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RegistrationFormValidatorTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val registrationObservable: RegistrationObservable = mock()
    private val patternMatcher: PatternMatcher = mock()
    private val registrationFormValidator = RegistrationFormValidator(
        patternMatcher = patternMatcher,
        initialValidState = null,
    )

    @Test
    fun firstNameChangedPostInvalidIfBlankThenValidIfNotBlank() {
        whenever(registrationObservable.getFirstName()).thenReturn("").thenReturn("name")
        val result = registrationFormValidator.registrationValid().test()
        registrationFormValidator.onFirstNameChanged(registrationObservable)
        registrationFormValidator.onFirstNameChanged(registrationObservable)
        result.assertValues(
            RegistrationValid(
                firstNameValid = false,
                firstNameError = R.string.first_name_invalid,
            ),
            RegistrationValid(
                firstNameValid = true,
            )
        )
    }

    @Test
    fun lastNameChangedPostInvalidIfBlankThenValidIfNotBlank() {
        whenever(registrationObservable.getLastName()).thenReturn("").thenReturn("name")
        val result = registrationFormValidator.registrationValid().test()
        registrationFormValidator.onLastNameChanged(registrationObservable)
        registrationFormValidator.onLastNameChanged(registrationObservable)
        result.assertValues(
            RegistrationValid(
                lastNameValid = false,
                lastNameError = R.string.last_name_invalid,
            ),
            RegistrationValid(
                lastNameValid = true,
            )
        )
    }

    @Test
    fun emailChangedPostInvalidIfBlankThenInvalidIfDoesNotMatchPatternThenValidIfMatchesPattern() {
        whenever(registrationObservable.getEmail())
            .thenReturn("")
            .thenReturn("email")
            .thenReturn("email@valid.com")
        whenever(patternMatcher.isEmail("email@valid.com")).thenReturn(true)
        val result = registrationFormValidator.registrationValid().test()
        registrationFormValidator.onEmailChanged(registrationObservable)
        registrationFormValidator.onEmailChanged(registrationObservable)
        registrationFormValidator.onEmailChanged(registrationObservable)
        result.assertValues(
            RegistrationValid(
                emailValid = false,
                emailError = R.string.email_invalid,
            ),
            RegistrationValid(
                emailValid = false,
                emailError = R.string.email_invalid,
            ),
            RegistrationValid(
                emailValid = true,
            )
        )
    }

    @Test
    fun passwordChangedPostInvalidIfBlankThenValidIfNotBlank() {
        whenever(registrationObservable.getPassword()).thenReturn("").thenReturn("password")
        val result = registrationFormValidator.registrationValid().test()
        registrationFormValidator.onPasswordChanged(registrationObservable)
        registrationFormValidator.onPasswordChanged(registrationObservable)
        result.assertValues(
            RegistrationValid(
                passwordValid = false,
                passwordError = R.string.password_invalid,
            ),
            RegistrationValid(
                passwordValid = true,
            )
        )
    }

    @Test
    fun confirmPasswordChangedPostInvalidIfBlankThenInvalidIfNotMatchingPasswordThenValidIfMatches() {
        whenever(registrationObservable.getPassword()).thenReturn("password")
        whenever(registrationObservable.getConfirmPassword())
            .thenReturn("")
            .thenReturn("pass")
            .thenReturn("password")
        val result = registrationFormValidator.registrationValid().test()
        registrationFormValidator.onConfirmPasswordChanged(registrationObservable)
        registrationFormValidator.onConfirmPasswordChanged(registrationObservable)
        registrationFormValidator.onConfirmPasswordChanged(registrationObservable)
        result.assertValues(
            RegistrationValid(
                confirmPasswordValid = false,
                confirmPasswordError = R.string.confirm_password_invalid,
            ),
            RegistrationValid(
                confirmPasswordValid = false,
                confirmPasswordError = R.string.confirm_password_does_not_match,
            ),
            RegistrationValid(
                confirmPasswordValid = true
            )
        )
    }
}
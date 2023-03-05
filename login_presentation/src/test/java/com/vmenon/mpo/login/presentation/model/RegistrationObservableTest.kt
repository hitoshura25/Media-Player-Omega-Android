package com.vmenon.mpo.login.presentation.model

import com.vmenon.mpo.login.presentation.RegistrationFormValidator
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions

class RegistrationObservableTest {
    private val registrationFormValidator: RegistrationFormValidator = mock()
    private val registrationForm = RegistrationForm()
    private val registrationObservable = RegistrationObservable(
        registrationFormValidator,
        registrationForm
    )

    @Test
    fun firstNameChangedDoesNothingIfFirstNameNotChanged() {
        registrationForm.firstName = "name"
        registrationObservable.firstNameChanged("name", 0, 0, 4)
        verifyNoInteractions(registrationFormValidator)
    }

    @Test
    fun lastNameChangedDoesNothingIfLastNameNotChanged() {
        registrationForm.lastName = "name"
        registrationObservable.lastNameChanged("name", 0, 0, 4)
        verifyNoInteractions(registrationFormValidator)
    }

    @Test
    fun emailChangedDoesNothingIfEmailNotChanged() {
        registrationForm.email = "email"
        registrationObservable.emailChanged("email", 0, 0, 5)
        verifyNoInteractions(registrationFormValidator)
    }

    @Test
    fun passwordChangedDoesNothingIfPasswordNotChanged() {
        registrationForm.password = "password"
        registrationObservable.passwordChanged("password", 0, 0, 8)
        verifyNoInteractions(registrationFormValidator)
    }

    @Test
    fun confirmPasswordChangedDoesNothingIfPasswordNotChanged() {
        registrationForm.confirmPassword = "password"
        registrationObservable.confirmPasswordChanged("password", 0, 0, 8)
        verifyNoInteractions(registrationFormValidator)
    }
}
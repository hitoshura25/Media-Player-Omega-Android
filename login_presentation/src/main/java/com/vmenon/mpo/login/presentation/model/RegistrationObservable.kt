package com.vmenon.mpo.login.presentation.model

import com.vmenon.mpo.login.presentation.RegistrationFormValidator

class RegistrationObservable(private val validator: RegistrationFormValidator) {
    private val registrationForm = RegistrationForm()

    fun getFirstName() = registrationForm.firstName
    fun firstNameChanged(input: CharSequence, start: Int, before: Int, count: Int) {
        val value = input.toString()
        if (registrationForm.firstName != value) {
            registrationForm.firstName = value
            validator.onFirstNameChanged(this)
        }
    }

    fun getLastName() = registrationForm.lastName
    fun lastNameChanged(input: CharSequence, start: Int, before: Int, count: Int) {
        val value = input.toString()
        if (registrationForm.lastName != value) {
            registrationForm.lastName = value
            validator.onLastNameChanged(this)
        }
    }

    fun getEmail() = registrationForm.email
    fun emailChanged(input: CharSequence, start: Int, before: Int, count: Int) {
        val value = input.toString()
        if (registrationForm.email != value) {
            registrationForm.email = value
            validator.onEmailChanged(this)
        }
    }

    fun getPassword() = registrationForm.password
    fun passwordChanged(input: CharSequence, start: Int, before: Int, count: Int) {
        val value = input.toString()
        if (registrationForm.password != value) {
            registrationForm.password = value
            validator.onPasswordChanged(this)
        }
    }

    fun getConfirmPassword() = registrationForm.confirmPassword
    fun confirmPasswordChanged(input: CharSequence, start: Int, before: Int, count: Int) {
        val value = input.toString()
        if (registrationForm.confirmPassword != value) {
            registrationForm.confirmPassword = value
            validator.onConfirmPasswordChanged(this)
        }
    }
}
package com.vmenon.mpo.login.presentation

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vmenon.mpo.login.presentation.model.RegistrationObservable
import com.vmenon.mpo.login.presentation.model.RegistrationValid

class RegistrationFormValidator {
    private val registrationValid = MutableLiveData(INITIAL_VALID_STATE)

    fun registrationValid(): LiveData<RegistrationValid> = registrationValid

    fun onFirstNameChanged(sender: RegistrationObservable) {
        val firstNameError =
            if (sender.getFirstName().isNotBlank()) null else R.string.first_name_invalid
        registrationValid.postValue(
            registrationValid.value?.copy(
                firstNameValid = firstNameError == null,
                firstNameError = firstNameError
            ) ?: INITIAL_VALID_STATE
        )
    }

    fun onLastNameChanged(sender: RegistrationObservable) {
        val lastNameError =
            if (sender.getLastName().isNotBlank()) null else R.string.last_name_invalid
        registrationValid.postValue(
            registrationValid.value?.copy(
                lastNameValid = lastNameError == null,
                lastNameError = lastNameError
            ) ?: INITIAL_VALID_STATE
        )
    }

    fun onEmailChanged(sender: RegistrationObservable) {
        val emailError =
            if (sender.getEmail().isNotBlank()
                && Patterns.EMAIL_ADDRESS.matcher(
                    sender.getEmail()
                ).matches()
            ) null else R.string.email_invalid
        registrationValid.postValue(
            registrationValid.value?.copy(
                emailValid = emailError == null,
                emailError = emailError
            ) ?: INITIAL_VALID_STATE
        )
    }

    fun onPasswordChanged(sender: RegistrationObservable) {
        val passwordError =
            if (sender.getPassword().isNotBlank()) null else R.string.password_invalid
        registrationValid.postValue(
            registrationValid.value?.copy(
                passwordValid = passwordError == null,
                passwordError = passwordError
            ) ?: INITIAL_VALID_STATE
        )
    }

    fun onConfirmPasswordChanged(sender: RegistrationObservable) {
        val confirmPasswordError =
            if (sender.getConfirmPassword().isNotBlank()) {
                if (sender.getConfirmPassword() ==
                    sender.getPassword()
                ) {
                    null
                } else {
                    R.string.confirm_password_does_not_match
                }
            } else {
                R.string.confirm_password_invalid
            }
        registrationValid.postValue(
            registrationValid.value?.copy(
                confirmPasswordValid = confirmPasswordError == null,
                confirmPasswordError = confirmPasswordError
            ) ?: INITIAL_VALID_STATE
        )
    }

    companion object {
        private val INITIAL_VALID_STATE = RegistrationValid()
    }
}
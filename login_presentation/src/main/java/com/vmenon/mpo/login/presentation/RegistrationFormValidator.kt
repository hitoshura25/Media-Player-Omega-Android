package com.vmenon.mpo.login.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vmenon.mpo.login.presentation.model.RegistrationObservable
import com.vmenon.mpo.login.presentation.model.RegistrationValid
import com.vmenon.mpo.system.domain.PatternMatcher

class RegistrationFormValidator(
    private val patternMatcher: PatternMatcher,
    initialValidState: RegistrationValid? = INITIAL_VALID_STATE,
) {
    private val registrationValid =
        initialValidState?.let { MutableLiveData(it) } ?: MutableLiveData()

    fun registrationValid(): LiveData<RegistrationValid> = registrationValid

    fun onFirstNameChanged(sender: RegistrationObservable) {
        val firstNameError =
            if (sender.getFirstName().isNotBlank()) null else R.string.first_name_invalid
        registrationValid.postValue(
            (registrationValid.value ?: INITIAL_VALID_STATE).copy(
                firstNameValid = firstNameError == null,
                firstNameError = firstNameError
            )
        )
    }

    fun onLastNameChanged(sender: RegistrationObservable) {
        val lastNameError =
            if (sender.getLastName().isNotBlank()) null else R.string.last_name_invalid
        registrationValid.postValue(
            (registrationValid.value ?: INITIAL_VALID_STATE).copy(
                lastNameValid = lastNameError == null,
                lastNameError = lastNameError
            )
        )
    }

    fun onEmailChanged(sender: RegistrationObservable) {
        val email = sender.getEmail()
        val emailError = if (email.isNotBlank() && patternMatcher.isEmail(email)) {
            null
        } else {
            R.string.email_invalid
        }
        registrationValid.postValue(
            (registrationValid.value ?: INITIAL_VALID_STATE).copy(
                emailValid = emailError == null,
                emailError = emailError
            )
        )
    }

    fun onPasswordChanged(sender: RegistrationObservable) {
        val passwordError =
            if (sender.getPassword().isNotBlank()) null else R.string.password_invalid
        registrationValid.postValue(
            (registrationValid.value ?: INITIAL_VALID_STATE).copy(
                passwordValid = passwordError == null,
                passwordError = passwordError
            )
        )
    }

    fun onConfirmPasswordChanged(sender: RegistrationObservable) {
        val confirmPassword = sender.getConfirmPassword()
        val confirmPasswordError =
            if (confirmPassword.isNotBlank()) {
                if (confirmPassword == sender.getPassword()) {
                    null
                } else {
                    R.string.confirm_password_does_not_match
                }
            } else {
                R.string.confirm_password_invalid
            }
        registrationValid.postValue(
            (registrationValid.value ?: INITIAL_VALID_STATE).copy(
                confirmPasswordValid = confirmPasswordError == null,
                confirmPasswordError = confirmPasswordError
            )
        )
    }

    companion object {
        private val INITIAL_VALID_STATE = RegistrationValid()
    }
}
package com.vmenon.mpo.login_feature.model

data class RegistrationValid(
    val firstNameValid: Boolean = false,
    val firstNameError: Int? = null,
    val lastNameValid: Boolean = false,
    val lastNameError: Int? = null,
    val emailValid: Boolean = false,
    val emailError: Int? = null,
    val passwordValid: Boolean = false,
    val passwordError: Int? = null,
    val confirmPasswordValid: Boolean = false,
    val confirmPasswordError: Int? = null
) {
    fun allValid() =
        firstNameValid && lastNameValid && emailValid && passwordValid && confirmPasswordValid
}

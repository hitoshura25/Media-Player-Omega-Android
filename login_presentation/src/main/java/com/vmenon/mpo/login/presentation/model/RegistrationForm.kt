package com.vmenon.mpo.login.presentation.model

data class RegistrationForm(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = ""
)

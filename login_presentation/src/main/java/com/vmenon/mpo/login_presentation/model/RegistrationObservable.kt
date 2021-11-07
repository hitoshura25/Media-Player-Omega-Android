package com.vmenon.mpo.login_presentation.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.vmenon.mpo.login_feature.BR

class RegistrationObservable : BaseObservable() {
    private val registrationForm = RegistrationForm()

    @Bindable
    fun getFirstName(): String {
        return registrationForm.firstName
    }

    fun setFirstName(value: String) {
        if (registrationForm.firstName != value) {
            registrationForm.firstName = value
            notifyPropertyChanged(BR.firstName)
        }
    }

    @Bindable
    fun getLastName(): String {
        return registrationForm.lastName
    }

    fun setLastName(value: String) {
        if (registrationForm.lastName != value) {
            registrationForm.lastName = value
            notifyPropertyChanged(BR.lastName)
        }
    }

    @Bindable
    fun getEmail(): String {
        return registrationForm.email
    }

    fun setEmail(value: String) {
        if (registrationForm.email != value) {
            registrationForm.email = value
            notifyPropertyChanged(BR.email)
        }
    }

    @Bindable
    fun getPassword(): String {
        return registrationForm.password
    }

    fun setPassword(value: String) {
        if (registrationForm.password != value) {
            registrationForm.password = value
            notifyPropertyChanged(BR.password)
        }
    }

    @Bindable
    fun getConfirmPassword(): String {
        return registrationForm.confirmPassword
    }

    fun setConfirmPassword(value: String) {
        if (registrationForm.confirmPassword != value) {
            registrationForm.confirmPassword = value
            notifyPropertyChanged(BR.confirmPassword)
        }
    }
}
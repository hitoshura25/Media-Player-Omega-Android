package com.vmenon.mpo.login.data

interface UserSettings {
    suspend fun enrolledInBiometrics(): Boolean
    suspend fun setEnrolledInBiometrics(enrolled: Boolean)
}
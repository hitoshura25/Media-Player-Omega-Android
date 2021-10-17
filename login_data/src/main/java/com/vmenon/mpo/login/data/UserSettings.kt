package com.vmenon.mpo.login.data

interface UserSettings {
    suspend fun clear()
    suspend fun enrolledInBiometrics(): Boolean
    suspend fun setEnrolledInBiometrics(enrolled: Boolean)
    suspend fun userDeclinedBiometrics(): Boolean
    suspend fun setUserDeclinedBiometrics(declined: Boolean)
    suspend fun hasAskedToEnrollInBiometrics(): Boolean
    suspend fun askedToEnrollInBiometrics()
}
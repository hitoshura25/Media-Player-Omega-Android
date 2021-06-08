package com.vmenon.mpo.login.data

interface Authenticator {
    fun startAuthentication(context: Any)
    fun logout()
}
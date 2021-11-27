package com.vmenon.mpo.auth.domain

class AuthException(cause: Throwable, message: String? = null) : Exception(message, cause)
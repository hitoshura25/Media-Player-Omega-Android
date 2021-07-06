package com.vmenon.mpo.api.model

import java.io.Serializable

data class UserDetails(val firstName: String, val lastName: String, val email: String)  :
    Serializable
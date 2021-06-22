package com.vmenon.mpo.login.data

import com.vmenon.mpo.login.domain.User

interface UserCache {
    suspend fun getCachedUser(): User?
    suspend fun cacheUser(user: User)
    suspend fun clear()
}
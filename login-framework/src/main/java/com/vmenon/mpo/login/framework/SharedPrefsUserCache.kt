package com.vmenon.mpo.login.framework

import com.vmenon.mpo.login.data.UserCache
import com.vmenon.mpo.login.domain.User

class SharedPrefsUserCache : UserCache {
    override suspend fun getCachedUser(): User? {
        TODO("Not yet implemented")
    }

    override suspend fun cacheUser(user: User) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_user"
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val ACCESS_TOKEN_EXPIRATION = "access_token_expiration"
        private const val ID_TOKEN = "id_token"
        private const val TOKEN_TYPE = "token_type"
    }
}
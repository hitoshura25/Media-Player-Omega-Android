package com.vmenon.mpo.login.framework

import android.content.Context
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.data.UserCache
import com.vmenon.mpo.login.domain.User

class SharedPrefsUserCache(
    context: Context,
    private val system: System,
    private val cacheExpirationMillis: Long
) : UserCache {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    private var currentUser: CachedUser? = loadFromPrefs()

    override suspend fun getCachedUser(): User? = if (isUserValid()) currentUser?.user else null

    override suspend fun cacheUser(user: User) {
        val cachedUser = CachedUser(user, system.currentTimeMillis())
        storeToPrefs(cachedUser)
        currentUser = cachedUser
    }

    override suspend fun clear() {
        currentUser = null
        sharedPrefs.edit().clear().apply()
    }

    private fun isUserValid(): Boolean = currentUser?.let { currentUser ->
        system.currentTimeMillis() - currentUser.lastUpdated < cacheExpirationMillis
    } ?: false


    private fun loadFromPrefs(): CachedUser? {
        val email = sharedPrefs.getString(EMAIL, null)
        val firstName = sharedPrefs.getString(FIRST_NAME, null)
        val lastName = sharedPrefs.getString(LAST_NAME, null)
        val lastUpdated = sharedPrefs.getLong(LAST_UPDATED, system.currentTimeMillis())
        return if (email != null && firstName != null && lastName != null) {
            CachedUser(User(email, firstName, lastName), lastUpdated)
        } else {
            null
        }
    }

    private fun storeToPrefs(cachedUser: CachedUser) {
        sharedPrefs.edit()
            .putString(EMAIL, cachedUser.user.email)
            .putString(FIRST_NAME, cachedUser.user.firstName)
            .putString(LAST_NAME, cachedUser.user.lastName)
            .putLong(LAST_UPDATED, cachedUser.lastUpdated)
            .apply()
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_user"
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val EMAIL = "email"
        private const val LAST_UPDATED = "last_updated"

        private data class CachedUser(val user: User, val lastUpdated: Long)
    }
}
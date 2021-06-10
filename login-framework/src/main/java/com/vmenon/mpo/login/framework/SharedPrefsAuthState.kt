package com.vmenon.mpo.login.framework

import android.content.Context
import com.vmenon.mpo.login.data.AuthState
import com.vmenon.mpo.login.domain.Credentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SharedPrefsAuthState(context: Context) : AuthState {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    private var storedCredentials: Credentials? = readFromSharedPrefs()
    private set(value) {
        field = value
        credentialState.value = value
    }

    private val credentialState = MutableStateFlow(storedCredentials)

    override fun getCredentials(): Credentials? = storedCredentials
    override fun credentials(): Flow<Credentials?> = credentialState

    override suspend fun storeCredentials(credentials: Credentials) {
        storeToSharedPrefs(credentials)
        this.storedCredentials = credentials
    }

    override suspend fun clearCredentials() {
        sharedPreferences.edit().clear().apply()
        this.storedCredentials = null
    }

    private fun readFromSharedPrefs(): Credentials? {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)
        val accessTokenExpiration = sharedPreferences.getLong(ACCESS_TOKEN_EXPIRATION, -1L)
        val idToken = sharedPreferences.getString(ID_TOKEN, null)
        val tokenType = sharedPreferences.getString(TOKEN_TYPE, null)

        return if (
            accessToken != null
            && refreshToken != null
            && accessTokenExpiration != -1L
            && idToken != null
            && tokenType != null
        ) {
            Credentials(
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiration = accessTokenExpiration,
                idToken = idToken,
                tokenType = tokenType
            )
        } else null
    }

    private fun storeToSharedPrefs(credentials: Credentials) {
        sharedPreferences.edit()
            .putString(ACCESS_TOKEN, credentials.accessToken)
            .putString(REFRESH_TOKEN, credentials.refreshToken)
            .putString(ID_TOKEN, credentials.idToken)
            .putLong(ACCESS_TOKEN_EXPIRATION, credentials.accessTokenExpiration)
            .putString(TOKEN_TYPE, credentials.tokenType)
            .apply()
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_credentials"
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN_EXPIRATION = "access_token_expiration"
        private const val ID_TOKEN = "id_token"
        private const val TOKEN_TYPE = "token_type"
    }
}
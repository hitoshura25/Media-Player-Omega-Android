package com.vmenon.mpo.auth.framework

import android.content.Context
import com.google.gson.Gson
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class SharedPrefsAuthState(
    context: Context,
    biometricsManager: BiometricsManager
) : AuthState {
    private val cryptographyManager = CryptographyManager()
    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_FILE,
        Context.MODE_PRIVATE
    )
    private var biometricCipher: Cipher? = null
    private val credentialState = MutableSharedFlow<CredentialsResult>(replay = 1)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var storedCredentials: CredentialsResult? = null
        private set(value) {
            field = value
            scope.launch {
                credentialState.emit(value ?: CredentialsResult.None)
            }
        }

    init {
        scope.launch {
            getCredentials()
            biometricsManager.authenticated().collect { cipher ->
                if (biometricCipher != cipher) {
                    biometricCipher = cipher
                    sharedPreferences.edit().putBoolean(ENCRYPTED_WITH_BIOMETRICS, true).apply()
                    when (val credentialsResult = getCredentials()) {
                        is CredentialsResult.Success -> {
                            storeToSharedPrefs(credentialsResult.credentials)
                        }
                        is CredentialsResult.RequiresBiometricAuth -> {
                            storedCredentials = readFromSharedPrefs()
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCredentials(): CredentialsResult {
        if (storedCredentials == null) {
            storedCredentials = readFromSharedPrefs()
        }
        return storedCredentials ?: CredentialsResult.None
    }

    override fun credentials(): Flow<CredentialsResult> = credentialState

    override suspend fun storeCredentials(credentials: Credentials) {
        storeToSharedPrefs(credentials)
        this.storedCredentials = CredentialsResult.Success(credentials)
    }

    override suspend fun clearCredentials() {
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        if (!encrypted) {
            sharedPreferences.edit().clear().apply()
        }
        this.storedCredentials = null
    }

    private fun readFromSharedPrefs(): CredentialsResult {
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        val credentialsJson = sharedPreferences.getString(CREDENTIALS, null)
        return if (credentialsJson != null) {
            if (encrypted) {
                biometricCipher?.let { biometricCipher ->
                    val cipherWrapper = gson.fromJson(
                        credentialsJson,
                        CiphertextWrapper::class.java
                    )
                    val decrypted = cryptographyManager.decryptData(
                        cipherWrapper.ciphertext,
                        biometricCipher
                    )
                    CredentialsResult.Success(gson.fromJson(decrypted, Credentials::class.java))
                } ?: CredentialsResult.RequiresBiometricAuth
            } else {
                CredentialsResult.Success(gson.fromJson(credentialsJson, Credentials::class.java))
            }
        } else CredentialsResult.None
    }

    private fun storeToSharedPrefs(credentials: Credentials) {
        val credentialJSON = gson.toJson(credentials)
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        if (encrypted) {
            biometricCipher?.let { biometricCipher ->
                val cipherWrapper = cryptographyManager.encryptData(credentialJSON, biometricCipher)
                val encryptedJSON = Gson().toJson(cipherWrapper)
                sharedPreferences.edit().putString(CREDENTIALS, encryptedJSON).apply()
            }
        } else {
            sharedPreferences.edit().putString(CREDENTIALS, credentialJSON).apply()
        }
    }

    private data class CiphertextWrapper(
        val ciphertext: ByteArray,
        val initializationVector: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CiphertextWrapper

            if (!ciphertext.contentEquals(other.ciphertext)) return false
            if (!initializationVector.contentEquals(other.initializationVector)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = ciphertext.contentHashCode()
            result = 31 * result + initializationVector.contentHashCode()
            return result
        }
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_credentials"
        private const val CREDENTIALS = "credentials"
        private const val ENCRYPTED_WITH_BIOMETRICS = "encrypted_with_biometrics"
    }
}
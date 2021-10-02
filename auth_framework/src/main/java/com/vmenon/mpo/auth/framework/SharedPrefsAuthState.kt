package com.vmenon.mpo.auth.framework

import android.content.Context
import com.google.gson.Gson
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.CipherEncryptedData
import com.vmenon.mpo.auth.domain.Credentials
import com.vmenon.mpo.auth.domain.CredentialsResult
import com.vmenon.mpo.auth.domain.CredentialsResult.RequiresBiometricAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class SharedPrefsAuthState(context: Context) : AuthState {
    private val cryptographyManager = CryptographyManager()
    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_FILE,
        Context.MODE_PRIVATE
    )
    private var biometricEncryptionCipher: Cipher? = null
    private var biometricDecryptionCipher: Cipher? = null

    private val credentialState = MutableSharedFlow<CredentialsResult>(replay = 1)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var storedCredentials: CredentialsResult? = null
        private set(value) {
            if (field != value) {
                field = value
                scope.launch {
                    credentialState.emit(value ?: CredentialsResult.None)
                }
            }
        }

    init {
        scope.launch {
            getCredentials()
        }
    }

    override suspend fun getCredentials(): CredentialsResult {
        if (storedCredentials == null || storedCredentials is RequiresBiometricAuth) {
            storedCredentials = readFromSharedPrefs()
        }
        return storedCredentials ?: CredentialsResult.None
    }

    override fun credentials(): Flow<CredentialsResult> = credentialState
    override suspend fun isLoggedOut(): Boolean = sharedPreferences.getBoolean(SIGNED_OUT, true)

    override suspend fun storeCredentials(credentials: Credentials) {
        storeToSharedPrefs(credentials)
        this.storedCredentials = CredentialsResult.Success(credentials)
    }

    override suspend fun clearCredentials() {
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        if (!encrypted) {
            sharedPreferences.edit().clear().apply()
        }
        sharedPreferences.edit().putBoolean(SIGNED_OUT, true).apply()
        this.storedCredentials = null
        this.biometricDecryptionCipher = null
        this.biometricEncryptionCipher = null
    }

    override suspend fun encryptCredentials(cipher: Cipher) {
        if (biometricEncryptionCipher != cipher) {
            biometricEncryptionCipher = cipher
            sharedPreferences.edit().putBoolean(ENCRYPTED_WITH_BIOMETRICS, true).apply()
            when (val credentialsResult = getCredentials()) {
                is CredentialsResult.Success -> storeToSharedPrefs(
                    credentialsResult.credentials
                )
            }
        }
    }

    override suspend fun decryptCredentials(cipher: Cipher) {
        if (biometricDecryptionCipher != cipher) {
            biometricDecryptionCipher = cipher
        }
        getCredentials()
    }

    private fun readFromSharedPrefs(): CredentialsResult {
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        val credentialsJson = sharedPreferences.getString(CREDENTIALS, null)
        return if (credentialsJson != null) {
            if (encrypted) {
                val encryptedCredentials = gson.fromJson(
                    credentialsJson,
                    CipherEncryptedData::class.java
                )
                biometricDecryptionCipher?.let { biometricCipher ->
                    val decrypted = cryptographyManager.decryptData(
                        encryptedCredentials.ciphertext,
                        biometricCipher
                    )
                    CredentialsResult.Success(gson.fromJson(decrypted, Credentials::class.java))
                } ?: RequiresBiometricAuth(encryptedCredentials)
            } else {
                CredentialsResult.Success(gson.fromJson(credentialsJson, Credentials::class.java))
            }
        } else CredentialsResult.None
    }

    private fun storeToSharedPrefs(credentials: Credentials) {
        val credentialJSON = gson.toJson(credentials)
        val encrypted = sharedPreferences.getBoolean(ENCRYPTED_WITH_BIOMETRICS, false)
        if (encrypted) {
            biometricEncryptionCipher?.let { biometricCipher ->
                val cipherWrapper = cryptographyManager.encryptData(credentialJSON, biometricCipher)
                val encryptedJSON = Gson().toJson(cipherWrapper)
                sharedPreferences.edit().putString(CREDENTIALS, encryptedJSON).apply()
            }
        } else {
            sharedPreferences.edit().putString(CREDENTIALS, credentialJSON).apply()
        }
        sharedPreferences.edit().putBoolean(SIGNED_OUT, false).apply()
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_credentials"
        private const val CREDENTIALS = "credentials"
        private const val ENCRYPTED_WITH_BIOMETRICS = "encrypted_with_biometrics"
        private const val SIGNED_OUT = "signed_out"
    }
}
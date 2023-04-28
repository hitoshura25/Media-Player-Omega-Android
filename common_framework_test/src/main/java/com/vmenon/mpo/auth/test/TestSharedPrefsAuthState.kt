package com.vmenon.mpo.auth.test

import android.content.Context
import com.vmenon.mpo.auth.framework.SharedPrefsAuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TestSharedPrefsAuthState(context: Context) : SharedPrefsAuthState(context) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun makeRequiresAuthState() {
        scope.launch {
            biometricDecryptionCipher = null
            storedCredentials = null
            getCredentials()
            println("Stored credentials: $storedCredentials")
        }
    }
}
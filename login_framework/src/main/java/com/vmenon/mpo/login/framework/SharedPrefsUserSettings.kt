package com.vmenon.mpo.login.framework

import android.content.Context
import com.vmenon.mpo.login.data.UserSettings

class SharedPrefsUserSettings(context: Context) : UserSettings {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    override suspend fun enrolledInBiometrics(): Boolean = sharedPrefs.getBoolean(
        ENROLLED_IN_BIOMETRICS, false
    )

    override suspend fun setEnrolledInBiometrics(enrolled: Boolean) {
        sharedPrefs.edit().putBoolean(ENROLLED_IN_BIOMETRICS, enrolled).apply()
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_user_settings"
        private const val ENROLLED_IN_BIOMETRICS = "enrolled_in_biometrics"
    }
}
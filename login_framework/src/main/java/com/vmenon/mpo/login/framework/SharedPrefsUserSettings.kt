package com.vmenon.mpo.login.framework

import android.content.Context
import com.vmenon.mpo.login.data.UserSettings

class SharedPrefsUserSettings(context: Context) : UserSettings {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    override suspend fun clear() {
        sharedPrefs.edit().clear().apply()
    }

    override suspend fun enrolledInBiometrics(): Boolean =
        sharedPrefs.getBoolean(ENROLLED_IN_BIOMETRICS, false)

    override suspend fun setEnrolledInBiometrics(enrolled: Boolean) {
        sharedPrefs.edit().putBoolean(ENROLLED_IN_BIOMETRICS, enrolled).apply()
    }

    override suspend fun userDeclinedBiometrics(): Boolean =
        sharedPrefs.getBoolean(DECLINED_BIOMETRICS, false)

    override suspend fun setUserDeclinedBiometrics(declined: Boolean) {
        sharedPrefs.edit().putBoolean(DECLINED_BIOMETRICS, declined).apply()
    }

    override suspend fun hasAskedToEnrollInBiometrics(): Boolean =
        sharedPrefs.getBoolean(ASKED_TO_ENROLL_IN_BIOMETRICS, false)

    override suspend fun askedToEnrollInBiometrics() {
        sharedPrefs.edit().putBoolean(ASKED_TO_ENROLL_IN_BIOMETRICS, true).apply()
    }

    companion object {
        private const val SHARED_PREFS_FILE = "mpo_user_settings"
        private const val ENROLLED_IN_BIOMETRICS = "enrolled_in_biometrics"
        private const val DECLINED_BIOMETRICS = "declined_biometrics"
        private const val ASKED_TO_ENROLL_IN_BIOMETRICS = "asked_to_enroll_in_biometrics"
    }
}
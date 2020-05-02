package com.mpo.core

import android.content.Context
import com.facebook.stetho.Stetho
import com.vmenon.mpo.core.ThirdPartyIntegrator

class DebugThirdPartyIntegrator : ThirdPartyIntegrator {
    override fun initialize(context: Context) {
        Stetho.initializeWithDefaults(context)
    }
}
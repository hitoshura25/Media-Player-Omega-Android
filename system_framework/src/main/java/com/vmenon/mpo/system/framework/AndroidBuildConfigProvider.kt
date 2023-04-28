package com.vmenon.mpo.system.framework

import android.os.Build
import com.vmenon.mpo.system.domain.BuildConfigProvider

class AndroidBuildConfigProvider(
    private val version: String,
    private val buildNumber: String
) : BuildConfigProvider {
    override fun appVersion(): String = version
    override fun buildNumber(): String = buildNumber
    override fun sdkVersion(): Int = Build.VERSION.SDK_INT
}
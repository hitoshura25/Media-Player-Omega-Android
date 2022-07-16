package com.vmenon.mpo.system.domain

interface BuildConfigProvider {
    fun appVersion(): String
    fun buildNumber(): String
    fun sdkVersion(): Int
}
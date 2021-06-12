package com.vmenon.mpo.core

import com.vmenon.mpo.common.domain.System

class SystemImpl : System {
    override fun println(message: String) {
        // TODO: Maybe send to a cloud provider?
        kotlin.io.println(message)
    }

    override fun currentTimeMillis(): Long = java.lang.System.currentTimeMillis()

    override fun sleep(millis: Long) {
        Thread.sleep(millis)
    }

    override suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
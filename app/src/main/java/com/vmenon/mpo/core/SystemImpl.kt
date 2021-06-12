package com.vmenon.mpo.core

import com.vmenon.mpo.common.domain.System

class SystemImpl : System {
    override fun println(message: String, exception: Exception?) {
        // TODO: Maybe send to a cloud provider?
        if (exception != null) {
            kotlin.io.println("$message: $exception")
        } else {
            kotlin.io.println(message)
        }
    }

    override fun currentTimeMillis(): Long = java.lang.System.currentTimeMillis()

    override fun sleep(millis: Long) {
        Thread.sleep(millis)
    }

    override suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
package com.vmenon.mpo.system.framework

import com.vmenon.mpo.system.domain.Logger

class LoggerImpl : Logger {
    override fun println(message: String, exception: Exception?) {
        // TODO: Maybe send to a cloud provider?
        if (exception != null) {
            kotlin.io.println("$message: $exception")
        } else {
            kotlin.io.println(message)
        }
    }
}
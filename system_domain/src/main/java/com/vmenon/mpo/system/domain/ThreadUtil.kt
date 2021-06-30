package com.vmenon.mpo.system.domain

interface ThreadUtil {
    fun sleep(millis: Long)
    suspend fun delay(timeMillis: Long)
}
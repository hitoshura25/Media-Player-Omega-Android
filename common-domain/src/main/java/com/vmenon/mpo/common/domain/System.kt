package com.vmenon.mpo.common.domain

interface System {
    fun println(message: String, exception: Exception? = null)
    fun currentTimeMillis(): Long
    fun sleep(millis: Long)
    suspend fun delay(timeMillis: Long)
}
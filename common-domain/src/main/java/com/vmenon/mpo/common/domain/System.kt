package com.vmenon.mpo.common.domain

interface System {
    fun println(message: String)
    fun currentTimeMillis(): Long
    fun sleep(millis: Long)
    suspend fun delay(timeMillis: Long)
}
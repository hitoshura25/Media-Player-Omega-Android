package com.vmenon.mpo.system.domain

interface Logger {
    fun println(message: String, exception: Exception? = null)
}
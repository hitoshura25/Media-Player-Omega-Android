package com.vmenon.mpo.system.domain

interface Clock {
    fun currentTimeMillis(): Long
}
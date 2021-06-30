package com.vmenon.mpo.system.framework

import com.vmenon.mpo.system.domain.Clock

class ClockImpl : Clock {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
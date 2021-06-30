package com.vmenon.mpo.system.framework

import com.vmenon.mpo.system.domain.ThreadUtil

class ThreadUtilImpl : ThreadUtil {
    override fun sleep(millis: Long) {
        Thread.sleep(millis)
    }

    override suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
package com.vmenon.mpo.system.framework

import org.junit.Test

class ThreadUtilImplTest {
    private val threadUtil = ThreadUtilImpl()

    @Test
    fun testSleep() {
        threadUtil.sleep(0)
    }
}
package com.vmenon.mpo.system.framework

import org.junit.Test

class LoggerImplTest {
    private val logger = LoggerImpl()

    @Test
    fun testPrintlnNoException() {
        logger.println("Test message", null)
    }

    @Test
    fun testPrintlnWithException() {
        logger.println("Test message", IllegalArgumentException("Illegal argument"))
    }
}
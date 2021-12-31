package com.vmenon.mpo.test

import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

class TestListener : RunListener() {
    private val screenshotRule = ScreenshotRule()
    override fun testFailure(failure: Failure) {
        screenshotRule.failed(failure.exception, failure.description)
    }
}
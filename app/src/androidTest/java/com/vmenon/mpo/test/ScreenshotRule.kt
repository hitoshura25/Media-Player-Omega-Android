package com.vmenon.mpo.test

import android.graphics.Bitmap
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ScreenshotRule : TestWatcher() {
    public override fun failed(exception: Throwable, description: Description) {
        super.failed(exception, description)
        val filename = description.displayName
        val capture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG
        try {
            capture.process(setOf(BasicScreenCaptureProcessor()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
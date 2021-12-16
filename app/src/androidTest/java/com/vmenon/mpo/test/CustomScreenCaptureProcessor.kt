package com.vmenon.mpo.test

import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File

class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor() {
    init {
        mTag = "CustomScreenCaptureProcessor"
        mDefaultScreenshotPath = getNewFilename()
    }

    private fun getNewFilename(): File? {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
}
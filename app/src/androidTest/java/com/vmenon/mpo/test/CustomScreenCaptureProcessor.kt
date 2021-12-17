package com.vmenon.mpo.test

import android.os.Environment
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File

class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor() {
    init {
        mTag = "CustomScreenCaptureProcessor"
        mDefaultScreenshotPath = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ),
            "test_run_screenshots"
        )
        Log.i(mTag, "Screenshot path: ${mDefaultScreenshotPath.absolutePath}")
    }
}
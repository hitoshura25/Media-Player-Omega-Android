package com.vmenon.mpo.test

import android.util.Log
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor

class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor() {
    init {
        mTag = "CustomScreenCaptureProcessor"
        Log.i(mTag, "Screenshot path: ${mDefaultScreenshotPath.absolutePath}")
    }
}
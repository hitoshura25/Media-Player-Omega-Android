package com.vmenon.mpo

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CommonTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, CommonTestApplication::class.java.name, context)
    }
}
package com.vmenon.mpo.test

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull

open class BaseSteps {
    private val device: UiDevice
    private val selector = UiSelector()
    private val appPackage: String

    init {
        InstrumentationRegistry.getInstrumentation().apply {
            device = UiDevice.getInstance(this)
            appPackage = targetContext.packageName
        }
    }

    fun launchApp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(appPackage)!!.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun clickOn(resName: String) {
        find(resName)!!.click()
    }

    fun clickOnIfVisible(resName: String): Boolean {
        val element = find(resName)
        return if (element == null) {
            false
        } else {
            element.click()
            true
        }
    }

    fun clickOnTextIfVisible(text: String, timeout: Long = TRANSITION_TIMEOUT): Boolean {
        val element = findText(text, timeout)
        return if (element == null) {
            false
        } else {
            element.click()
            true
        }
    }

    fun clickOnContentDescription(
        description: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): Boolean {
        val element = findContentDescription(description, timeout)
        return if (element == null) {
            false
        } else {
            element.click()
            true
        }
    }

    fun find(
        resName: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? {
        val packageName: String
        val resourceId: String
        if (resName.indexOf(".") != -1) {
            val resNameStart = resName.lastIndexOf(".")
            packageName = resName.substring(0, resNameStart)
            resourceId = resName.substring(resNameStart + 1, resName.length)
        } else {
            packageName = appPackage
            resourceId = resName
        }
        return device.wait(
            Until.findObject(By.res(packageName, resourceId)), timeout
        )
    }

    fun findText(
        text: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? = device.wait(
        Until.findObject(By.text(text)), timeout
    )

    fun findContentDescription(
        description: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? = device.wait(
        Until.findObject(By.desc(description)), timeout
    )

    fun text(input: String, resName: String) {
        find(resName)!!.text = input
    }

    fun waitForApp(packageName: String = appPackage, timeout: Long = TRANSITION_TIMEOUT) {
        assertNotNull(device.wait(Until.findObject(By.pkg(packageName)), timeout))
    }

    fun waitFor(
        resName: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        assertNotNull(find(resName, timeout))
    }

    fun waitForText(
        text: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        assertNotNull(findText(text, timeout))
    }

    fun waitForContentDescription(
        description: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        assertNotNull(findContentDescription(description, timeout))
    }

    fun waitForBrowser() {
        waitForApp(CHROME_STABLE)
        acceptChromePrivacyOption()
    }

    // Apparently selector() is the only mechanism that works...for Chrome browser at least
    fun browserText(input: String, resName: String) {
        device.findObject(selector.resourceId(resName)).text = input
    }

    fun browserClickOn(resName: String) {
        device.findObject(selector.resourceId(resName)).click()
    }

    fun pressKeyCode(keyCode: Int) {
        device.pressKeyCode(keyCode)
    }

    @Throws(UiObjectNotFoundException::class)
    protected fun acceptChromePrivacyOption() {
        val selector = UiSelector()
        val accept: UiObject = device.findObject(selector.resourceId(ID_ACCEPT))
        accept.waitForExists(TRANSITION_TIMEOUT)
        if (accept.exists()) {
            accept.click()
        }
        val noThanks: UiObject = device.findObject(selector.resourceId(ID_NO_THANKS))
        noThanks.waitForExists(TRANSITION_TIMEOUT)
        if (noThanks.exists()) {
            noThanks.click()
        }
    }

    companion object {
        const val nav_accounts = "login_nav_graph"
        const val CHROME_STABLE = "com.android.chrome"
        const val TRANSITION_TIMEOUT = 4000L
        const val NETWORK_TIMEOUT = 20000L
        const val ID_NO_THANKS = "com.android.chrome:id/negative_button"
        const val ID_ACCEPT = "com.android.chrome:id/terms_accept"
        const val ID_CLOSE_BROWSER = "com.android.chrome:id/close_button"
    }
}
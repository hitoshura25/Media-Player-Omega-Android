package com.vmenon.mpo.test

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

    fun clickOn(resName: String, packageName: String = appPackage) {
        find(resName, packageName)!!.click()
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

    fun find(
        resName: String,
        packageName: String = appPackage,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? = device.wait(
        Until.findObject(By.res(packageName, resName)), timeout
    )

    fun text(input: String, resName: String, packageName: String = appPackage) {
        find(resName, packageName)!!.text = input
    }

    fun waitForApp(packageName: String = appPackage, timeout: Long = TRANSITION_TIMEOUT) {
        assertNotNull(device.wait(Until.findObject(By.pkg(packageName)), timeout))
    }

    fun waitFor(
        resName: String,
        packageName: String = appPackage,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        assertNotNull(device.wait(Until.findObject(By.res(packageName, resName)), timeout))
    }

    fun waitForBrowser() {
        waitForApp(CHROME_STABLE)
        acceptChromePrivacyOption()
    }

    // Apparently selector() is the only mechanism that works...for Chrome browser at least
    fun browserText(input: String, resName: String) {
        device.findObject(selector.resourceId(resName)).text = input
        //text(input, resName, CHROME_STABLE)
    }

    fun browserClickOn(resName: String) {
        device.findObject(selector.resourceId(resName)).click()
        //clickOn(resName, CHROME_STABLE)
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
        const val nav_accounts = "nav_account"
        const val CHROME_STABLE = "com.android.chrome"
        const val TRANSITION_TIMEOUT = 2000L
        const val NETWORK_TIMEOUT = 20000L
        const val ID_NO_THANKS = "com.android.chrome:id/negative_button"
        const val ID_ACCEPT = "com.android.chrome:id/terms_accept"
        const val ID_CLOSE_BROWSER = "com.android.chrome:id/close_button"
    }
}
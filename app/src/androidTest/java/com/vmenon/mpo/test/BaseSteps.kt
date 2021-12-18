package com.vmenon.mpo.test

import android.content.Context
import android.content.Intent
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

open class BaseSteps {
    private val device: UiDevice
    private val selector = UiSelector()
    protected val appPackage: String

    protected val mockWebDispatcher = MockWebDispatcher()
    protected val mockWebServer = MockWebServer().apply {
        dispatcher = mockWebDispatcher
    }

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

    fun waitForApp(packageName: String = appPackage, timeout: Long = APP_LAUNCH_TIMEOUT) {
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

    fun waitForDynamicFeatureToDownload() {
        assertTrue(device.wait(Until.gone(By.text(DYNAMIC_MODULE_LOADING)), DYNAMIC_MODULE_TIMEOUT))
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

    protected fun readFromAssets(fileName: String): String =
        InstrumentationRegistry.getInstrumentation().context.assets.open(fileName)
            .use { it.reader().readText() }

    protected fun getUnsignedIdToken(
        issuer: String,
        subject: String,
        audience: String,
        expiration: Long = (System.currentTimeMillis() / 1000) + (10 * 60).toLong(),
        issuedAt: Long = (System.currentTimeMillis() / 1000),
        nonce: String
    ): String {
        val header = JSONObject()
        header.put("typ", "JWT")
        val claims = JSONObject()
        claims.put("iss", issuer)
        claims.put("sub", subject)
        claims.put("aud", audience)
        claims.put("exp", expiration.toString())
        claims.put("iat", issuedAt.toString())
        claims.put("nonce", nonce)

        val encodedHeader: String = base64UrlNoPaddingEncode(header.toString().toByteArray())
        val encodedClaims: String = base64UrlNoPaddingEncode(claims.toString().toByteArray())
        return "$encodedHeader.$encodedClaims"
    }

    private fun base64UrlNoPaddingEncode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    inner class MockWebDispatcher : Dispatcher() {
        private val requestMap = HashMap<String, MockResponse>()

        fun setup(
            path: String,
            code: Int,
            bodyJSONFile: String,
            vars: Map<String, String>? = null
        ) {
            var bodyJSON = readFromAssets("responses/$bodyJSONFile")
            vars?.forEach { entry ->
                bodyJSON = bodyJSON.replace("\${${entry.key}}", entry.value)
            }
            requestMap[path] = MockResponse().setResponseCode(code).setBody(bodyJSON)
        }

        fun clear() {
            requestMap.clear()
        }

        override fun dispatch(request: RecordedRequest): MockResponse {
            println("MockWebDispatcher handling request: ${request.path}")
            return requestMap[request.path]!!
        }
    }

    companion object {
        const val nav_accounts = "login_nav_graph"
        const val CHROME_STABLE = "com.android.chrome"
        const val APP_LAUNCH_TIMEOUT = 8000L
        const val TRANSITION_TIMEOUT = 1000L
        const val DYNAMIC_MODULE_TIMEOUT = 5000L
        const val DYNAMIC_MODULE_LOADING = "Installing module:"
        const val NETWORK_TIMEOUT = 20000L
        const val ID_NO_THANKS = "com.android.chrome:id/negative_button"
        const val ID_ACCEPT = "com.android.chrome:id/terms_accept"
        const val ID_CLOSE_BROWSER = "com.android.chrome:id/close_button"
    }
}
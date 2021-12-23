package com.vmenon.mpo.test

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import kotlinx.coroutines.runBlocking
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
        log("clickOn $resName")
        find(resName)!!.click()
    }

    fun clickOnIfVisible(resName: String): Boolean {
        log("clickOnIfVisible $resName")
        val element = find(resName)
        return if (element == null) {
            false
        } else {
            element.click()
            true
        }
    }

    fun clickOnText(text: String, timeout: Long = TRANSITION_TIMEOUT) {
        log("clickOnText $text")
        findText(text, timeout)!!.click()
    }

    fun clickOnTextIfVisible(text: String, timeout: Long = TRANSITION_TIMEOUT): Boolean {
        log("clickOnTextIfVisible $text")
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
        log("clickOnContentDescription $description")
        val element = findContentDescription(description, timeout)
        return if (element == null) {
            false
        } else {
            element.click()
            true
        }
    }

    private fun find(
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

    private fun findText(
        text: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? = device.wait(
        Until.findObject(By.text(text)), timeout
    )

    private fun findContentDescription(
        description: String,
        timeout: Long = TRANSITION_TIMEOUT
    ): UiObject2? = device.wait(
        Until.findObject(By.desc(description)), timeout
    )

    fun text(input: String, resName: String) {
        log("text $input $resName")
        find(resName)!!.text = input
    }

    fun waitForApp(packageName: String = appPackage, timeout: Long = APP_LAUNCH_TIMEOUT) {
        log("waitForApp $packageName")
        assertNotNull(device.wait(Until.findObject(By.pkg(packageName)), timeout))
    }

    fun waitFor(
        resName: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        log("waitFor $resName")
        assertNotNull(find(resName, timeout))
    }

    fun waitForText(
        text: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        log("waitForText $text")
        assertNotNull(findText(text, timeout))
    }

    fun waitForTextToBeGone(
        text: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        log("waitForTextToBeGone $text")
        assertTrue(device.wait(Until.gone(By.text(text)), timeout))
    }

    fun waitForContentDescription(
        description: String,
        timeout: Long = TRANSITION_TIMEOUT
    ) {
        log("waitForTextContentDescription $description")
        assertNotNull(findContentDescription(description, timeout))
    }

    fun waitForBrowser() {
        waitForApp(CHROME_STABLE)
        acceptChromePrivacyOption()
    }

    fun waitForDynamicFeatureToDownload() {
        log("waitForDynamicFeatureToDownload")
        val condition =
            device.wait(Until.gone(By.text(DYNAMIC_MODULE_LOADING)), DYNAMIC_MODULE_TIMEOUT)
        log("Dynamic feature download complete: $condition")
        assertTrue(condition)
    }

    // Apparently selector() is the only mechanism that works...for Chrome browser at least
    fun browserText(input: String, resName: String) {
        log("browserText $input $resName")
        device.findObject(selector.resourceId(resName)).text = input
    }

    fun browserClickOn(resName: String) {
        log("browserClickOn $resName")
        device.findObject(selector.resourceId(resName)).click()
    }

    fun pressKeyCode(keyCode: Int) {
        log("pressKeyCode $keyCode")
        device.pressKeyCode(keyCode)
    }

    fun waitForEpisodeToDownload(episodeName: String) {
        val idlingResource = EpisodeDownloadedIdlingResource(episodeName)
        IdlingRegistry.getInstance().register(idlingResource)
        Espresso.onView(ViewMatchers.isRoot()).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        IdlingRegistry.getInstance().unregister(idlingResource)
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

    private fun log(message: String) {
        Log.d(LOG_TAG, message)
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
            log("MockWebDispatcher handling request: ${request.path}")
            return requestMap[request.path]!!
        }
    }

    inner class EpisodeDownloadedIdlingResource(private val episodeName: String) : IdlingResource {
        private var resourceCallback: IdlingResource.ResourceCallback? = null
        override fun getName(): String = EpisodeDownloadedIdlingResource::class.java.name

        override fun isIdleNow(): Boolean {
            val commonFrameworkComponentProvider =
                ApplicationProvider.getApplicationContext<Context>() as CommonFrameworkComponentProvider
            val commonFrameworkComponent =
                commonFrameworkComponentProvider.commonFrameworkComponent()
            val downloadDao = commonFrameworkComponent.downloadDao()
            val episodeDao = commonFrameworkComponent.episodeDao()

            return runBlocking {
                val episode = episodeDao.getByNameWithShowDetails(episodeName)!!
                val download = downloadDao.getByRequesterId(episode.episode.episodeId)
                val idle = download == null
                if (idle) {
                    resourceCallback?.onTransitionToIdle()
                }
                idle
            }
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            resourceCallback = callback
        }
    }

    companion object {
        const val nav_accounts = "login_nav_graph"
        const val CHROME_STABLE = "com.android.chrome"
        const val APP_LAUNCH_TIMEOUT = 8000L
        const val TRANSITION_TIMEOUT = 8000L
        const val DYNAMIC_MODULE_TIMEOUT = 5000L
        const val DYNAMIC_MODULE_LOADING = "Installing module:"
        const val NETWORK_TIMEOUT = 20000L
        const val ID_NO_THANKS = "com.android.chrome:id/negative_button"
        const val ID_ACCEPT = "com.android.chrome:id/terms_accept"
        const val ID_CLOSE_BROWSER = "com.android.chrome:id/close_button"
        const val LOG_TAG = "cucumber-android"
    }
}
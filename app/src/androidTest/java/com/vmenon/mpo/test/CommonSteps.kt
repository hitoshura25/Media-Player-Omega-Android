package com.vmenon.mpo.test

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert

class CommonSteps {
    lateinit var device: UiDevice

    private val selector = UiSelector()
    private val appPackage = InstrumentationRegistry.getInstrumentation().targetContext.packageName

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            appPackage
        )!!.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    @After
    fun cleanup() {

    }

    @Given("I have launched the app")
    fun i_start_the_app() {

    }

    @When("I enter {string} into the {string} webpage element")
    fun i_enter_into_webpage_element(input: String, elementName: String) {
        val element: UiObject = device.findObject(selector.resourceId(elementName))
        element.text = input
    }

    @When("I click on the {string} webpage element")
    fun i_click_on_webpage_element(elementName: String) {
        device.findObject(selector.resourceId(elementName)).click()
    }

    @When("I see a webpage with the {string} element")
    fun i_see_a_web_page_with_element(elementName: String) {
        device.wait(Until.findObject(By.pkg(CHROME_STABLE)), NETWORK_TIMEOUT)
        acceptChromePrivacyOption()
        Assert.assertTrue(
            device.findObject(selector.resourceId(elementName)).waitForExists(NETWORK_TIMEOUT)
        )
    }

    @When("I click on the {string} tab")
    fun i_click_on_tab(resName: String) {
        device.findObject(selector.resourceId("$appPackage:id/$resName")).click()
    }

    @When("I click on {string}")
    fun i_click_on(resName: String) {
        Espresso.onView(ViewMatchers.withResourceName(resName)).perform(ViewActions.click())
    }

    @When("I enter {string} into the {string} field")
    fun i_enter_into_field(input: String, resName: String) {
        Espresso.onView(ViewMatchers.withResourceName(resName))
            .perform(ViewActions.typeText(input))
    }

    @Then("I should see {string} on the display")
    fun i_should_see_s_on_the_display(s: String?) {
        device.wait(Until.findObject(By.res(s)), 500L)
        Espresso.onView(ViewMatchers.withResourceName(s)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Then("I should return to the app")
    fun i_should_return_to_app() {
        Assert.assertNotNull(
            device.wait(
                Until.findObject(
                    By.pkg(InstrumentationRegistry.getInstrumentation().targetContext.packageName)
                ),
                NETWORK_TIMEOUT
            )
        )
    }

    @Throws(UiObjectNotFoundException::class)
    private fun acceptChromePrivacyOption() {
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
        private const val CHROME_STABLE = "com.android.chrome"
        private const val TRANSITION_TIMEOUT = 2000L
        private const val NETWORK_TIMEOUT = 20000L
        private const val ID_NO_THANKS = "com.android.chrome:id/negative_button"
        private const val ID_ACCEPT = "com.android.chrome:id/terms_accept"
        private const val ID_CLOSE_BROWSER = "com.android.chrome:id/close_button"
    }
}
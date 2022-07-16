package com.vmenon.mpo.test

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.KeyEvent
import androidx.biometric.BiometricManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.vmenon.mpo.CucumberTestMPOApplication
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.hamcrest.core.AllOf

open class CommonSteps : BaseSteps() {
    @Before
    fun setup() {
        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(HeldCertificate.Companion.decode(readFromAssets("mockWebServer.pem")))
            .build()
        init()
        mockWebServer.useHttps(
            serverCertificates.sslSocketFactory(),
            tunnelProxy = false,
        )
        mockWebServer.start(8080)
        launchApp()
    }

    @After
    fun cleanup() {
        release()
        mockWebDispatcher.clear()
        mockWebServer.shutdown()
    }

    @Given("I have launched the app")
    fun i_start_the_app() {
        waitForApp()
    }

    @Given("The API responds to request {string} with code {int} and body {string}")
    fun the_api_responds_to_request(request: String, code: Int, responseFile: String) {
        mockWebDispatcher.setup(request, code, responseFile)
    }

    @Given("I have signed out of the app")
    fun i_have_signed_out_of_the_app() {
        clickOn(nav_accounts)
        waitForDynamicFeatureToDownload()
        i_choose_not_to_enroll_in_biometrics()
        clickOnIfVisible("com.vmenon.mpo.login_feature.logout_link")
        waitFor("com.vmenon.mpo.login_feature.login_link")
    }

    @Given("I have signed out of the app using mock authentication")
    fun i_have_signed_out_of_the_app_with_mock_authentication() {
        Intents.intending(
            AllOf.allOf(
                IntentMatchers.hasComponent(
                    ComponentName(
                        appPackage,
                        "net.openid.appauth.AuthorizationManagementActivity"
                    )
                )
            )
        ).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        clickOn(nav_accounts)
        waitForDynamicFeatureToDownload()
        i_choose_not_to_enroll_in_biometrics()
        clickOnIfVisible("com.vmenon.mpo.login_feature.logout_link")
        waitFor("com.vmenon.mpo.login_feature.login_link")
    }

    @Given("I have launched sign in in the app")
    fun i_have_launched_sign_in() {
        clickOn(nav_accounts)
        waitForDynamicFeatureToDownload()
        clickOn("com.vmenon.mpo.login_feature.login_link")
    }

    @Given("I am using mock authentication")
    fun i_am_using_mock_authentication() {
        mockWebDispatcher.setup(
            "/oauth2/default/v1/token", 200, "token_response.json",
            mapOf(
                Pair(
                    "id_token",
                    getUnsignedIdToken(
                        issuer = "https://localhost:8080/oauth2/default",
                        audience = "mock_client_id",
                        nonce = "mock_nonce",
                        subject = "SUBJECT"
                    )
                ),
                Pair("access_token", "mock_access_token"),
                Pair("refresh_token", "mock_refresh_token")
            )
        )
        Intents.intending(
            AllOf.allOf(
                IntentMatchers.hasComponent(
                    ComponentName(
                        appPackage,
                        "net.openid.appauth.AuthorizationManagementActivity"
                    )
                )
            )
        ).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent().apply {
                putExtra(
                    "net.openid.appauth.AuthorizationResponse",
                    readFromAssets("responses/authorization_response.json")
                )
            })
        )
    }

    @Given("I have launched sign in in the app using mock authentication")
    fun i_have_launched_sign_in_using_mock_authentication() {
        mockWebDispatcher.setup(
            "/oauth2/default/v1/token", 200, "token_response.json",
            mapOf(
                Pair(
                    "id_token",
                    getUnsignedIdToken(
                        issuer = "https://localhost:8080/oauth2/default",
                        audience = "mock_client_id",
                        nonce = "mock_nonce",
                        subject = "SUBJECT"
                    )
                ),
                Pair("access_token", "mock_access_token"),
                Pair("refresh_token", "mock_refresh_token")
            )
        )
        Intents.intending(
            AllOf.allOf(
                IntentMatchers.hasComponent(
                    ComponentName(
                        appPackage,
                        "net.openid.appauth.AuthorizationManagementActivity"
                    )
                )
            )
        ).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent().apply {
                putExtra(
                    "net.openid.appauth.AuthorizationResponse",
                    readFromAssets("responses/authorization_response.json")
                )
            })
        )
        clickOn(nav_accounts)
        waitForDynamicFeatureToDownload()
        clickOn("com.vmenon.mpo.login_feature.login_link")
        Intents.intending(
            AllOf.allOf(
                IntentMatchers.hasComponent(
                    ComponentName(
                        appPackage,
                        "net.openid.appauth.AuthorizationManagementActivity"
                    )
                )
            )
        )
    }

    @Given("I have completed sign in with username {string} and password {string}")
    fun i_have_signed_in_with_username_password(username: String, password: String) {
        waitForBrowser()
        browserText(username, "okta-signin-username")
        browserText(password, "okta-signin-password")
        browserClickOn("okta-signin-submit")
        waitForApp()
    }

    @When("I choose not to enroll in biometrics")
    fun i_choose_not_to_enroll_in_biometrics() {
        clickOnText("NO")
    }

    @Suppress("DEPRECATION")
    @When("I choose to enroll in biometrics")
    fun i_choose_to_enroll_in_biometrics() {
        val intentAction = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Settings.ACTION_BIOMETRIC_ENROLL
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> Settings.ACTION_FINGERPRINT_ENROLL
            else -> Settings.ACTION_SECURITY_SETTINGS
        }
        Intents.intending(
            AllOf.allOf(
                IntentMatchers.hasAction(intentAction)
            )
        ).respondWithFunction {
            (ApplicationProvider.getApplicationContext<Context>() as CucumberTestMPOApplication).mockBiometricsManager.mockedAuthType =
                BiometricManager.BIOMETRIC_SUCCESS
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        }
        clickOnText("YES")
    }

    // TODO: Currently unused, but maybe can be if we actually want to do REAL test of Biometrics
    @When("I setup my device pin with {string}")
    fun i_setup_device_pin_with(pin: String) {
        if (findText(text = "Re-enter your PIN", timeout = 200L) != null) {
            i_enter_into_field("1111", "com.android.settings.password_entry")
            i_press_enter()
        } else {
            i_click_on_text_containing("PIN")
            i_enter_into_field("1111", "com.android.settings.password_entry")
            i_click_on_text("NEXT")
            i_enter_into_field("1111", "com.android.settings.password_entry")
            i_click_on_text("CONFIRM")
            i_click_on_text("DONE")
        }

        i_click_on_text_until_text_is_displayed("MORE", "I AGREE")
        i_click_on_text("I AGREE")
    }

    @When("I click on {string}")
    fun i_click_on(resName: String) {
        clickOn(resName)
    }

    @When("I click on text {string}")
    fun i_click_on_text(text: String) {
        clickOnText(text)
    }

    @When("I click on text containing {string}")
    fun i_click_on_text_containing(text: String) {
        clickOnText(text = text, useSubstring = true)
    }

    @When("I click on {string} if visible")
    fun i_click_on_if_visible(resName: String) {
        clickOnIfVisible(resName)
    }

    @When("I enter {string} into the {string} field")
    fun i_enter_into_field(input: String, resName: String) {
        text(input, resName)
    }

    @When("I click on content description {string}")
    fun i_click_on_content_description(description: String) {
        clickOnContentDescription(description)
    }

    @When("I scroll and click on text {string}")
    fun i_scroll_and_click_on_text(text: String) {
        UiScrollable(UiSelector().scrollable(true)).getChildByText(
            UiSelector().text(text),
            text,
            true
        ).click()
    }

    @When("I press enter")
    fun i_press_enter() {
        pressKeyCode(KeyEvent.KEYCODE_ENTER)
    }

    @When("I click on text {string} until text {string} is displayed")
    fun i_click_on_text_until_text_is_displayed(clickOnText: String, displayText: String) {
        clickOnTextUntilOtherTextIsVisible(clickOnText, displayText)
    }

    @When("The dynamic feature module download completes")
    fun the_dynamic_feature_module_completes() {
        println("Waiting for Dynamic module")
        waitForDynamicFeatureToDownload()
        println("Dynamic module completed")
    }

    @When("I wait for the browser to launch")
    fun i_wait_for_the_browser_to_launch() {
        waitForBrowser()
    }

    @When("I enter {string} into the {string} field in the web browser")
    fun i_enter_into_web_browser_field(input: String, resName: String) {
        browserText(input, resName)
    }

    @When("I click on the {string} field in the web browser")
    fun i_click_on_web_browser_field(resName: String) {
        browserClickOn(resName)
    }

    @When("I wait to return to the app")
    fun i_wait_to_return_to_app() {
        waitForApp()
    }

    @When("I wait for episode {string} to finish downloading")
    fun i_wait_for_episode_to_finish_downloading(episodeName: String) {
        waitForEpisodeToDownload(episodeName)
    }

    @When("I go back")
    fun i_go_back() {
        pressBack()
    }

    @When("I click on center of field {string}")
    fun i_slide_seekbar_field(resName: String) {
        clickOnCenter(resName)
    }

    @Then("I should see {string} on the display")
    fun i_should_see_s_on_the_display(resName: String) {
        waitFor(resName)
    }

    @Then("I should see text {string} on the display")
    fun i_should_see_text_s_on_the_display(text: String) {
        waitForText(text)
    }

    @Then("I should not see text {string} on the display")
    fun i_should_not_see_text_s_on_the_display(text: String) {
        waitForTextToBeGone(text)
    }

    @Then("I should see content description {string} on the display")
    fun i_should_see_content_description_s_on_the_display(description: String) {
        waitForContentDescription(description)
    }
}
package com.vmenon.mpo.test

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Intent
import android.view.KeyEvent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.hamcrest.core.AllOf
import org.junit.Rule

open class CommonSteps : BaseSteps() {
    @Rule
    val runtimePermissionRule: GrantPermissionRule = grant(WRITE_EXTERNAL_STORAGE)

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

    }

    @Given("The API responds to request {string} with code {int} and body {string}")
    fun the_api_responds_to_request(request: String, code: Int, responseFile: String) {
        mockWebDispatcher.setup(request, code, responseFile)
    }

    @When("I click on {string}")
    fun i_click_on(resName: String) {
        clickOn(resName)
    }

    @When("I click on text {string}")
    fun i_click_on_text(text: String) {
        clickOnTextIfVisible(text)
    }

    @When("I enter {string} into the {string} field")
    fun i_enter_into_field(input: String, resName: String) {
        text(input, resName)
    }

    @When("I click on content description {string}")
    fun i_click_on_content_description(description: String) {
        clickOnContentDescription(description)
    }

    @When("I press enter")
    fun i_press_enter() {
        pressKeyCode(KeyEvent.KEYCODE_ENTER)
    }

    @Then("I should see {string} on the display")
    fun i_should_see_s_on_the_display(resName: String) {
        waitFor(resName)
    }

    @Then("I should see text {string} on the display")
    fun i_should_see_text_s_on_the_display(text: String) {
        waitForText(text)
    }

    @Then("I should see content description {string} on the display")
    fun i_should_see_content_description_s_on_the_display(description: String) {
        waitForContentDescription(description)
    }

    @Given("I have signed out of the app")
    fun i_have_signed_out_of_the_app() {
        clickOn(nav_accounts)
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
                        "com.vmenon.mpo",
                        "net.openid.appauth.AuthorizationManagementActivity"
                    )
                )
            )
        ).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        clickOn(nav_accounts)
        i_choose_not_to_enroll_in_biometrics()
        clickOnIfVisible("com.vmenon.mpo.login_feature.logout_link")
        waitFor("com.vmenon.mpo.login_feature.login_link")
    }

    @Given("I have launched sign in in the app")
    fun i_have_launched_sign_in() {
        clickOn(nav_accounts)
        clickOn("com.vmenon.mpo.login_feature.login_link")
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
                        "com.vmenon.mpo",
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
        clickOn("com.vmenon.mpo.login_feature.login_link")
    }

    @Given("I have completed sign in with username {string} and password {string}")
    fun i_have_signed_in_with_username_password(username: String, password: String) {
        waitForBrowser()
        browserText(username, "okta-signin-username")
        browserText(password, "okta-signin-password")
        browserClickOn("okta-signin-submit")
        waitForApp()
    }

    @Given("I choose not to enroll in biometrics")
    fun i_choose_not_to_enroll_in_biometrics() {
        clickOnTextIfVisible("NO")
    }
}
package com.vmenon.mpo.test

import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CommonSteps : BaseSteps() {
    private val mockWebDispatcher = MockWebDispatcher()
    private val mockWebServer = MockWebServer().apply {
        dispatcher = mockWebDispatcher
    }

    @Before
    fun setup() {
        mockWebServer.start(8080)
        launchApp()
    }

    @After
    fun cleanup() {
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

    inner class MockWebDispatcher : Dispatcher() {
        private val requestMap = HashMap<String, MockResponse>()

        fun setup(path: String, code: Int, bodyJSONFile: String) {
            val bodyJSON =
                InstrumentationRegistry.getInstrumentation().context.assets.open("responses/$bodyJSONFile")
                    .use { it.reader().readText() }
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
}
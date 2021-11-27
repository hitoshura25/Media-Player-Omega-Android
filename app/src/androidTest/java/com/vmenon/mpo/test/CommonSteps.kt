package com.vmenon.mpo.test

import android.view.KeyEvent
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class CommonSteps : BaseSteps() {

    @Before
    fun setup() {
        launchApp()
    }

    @After
    fun cleanup() {

    }

    @Given("I have launched the app")
    fun i_start_the_app() {

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
}
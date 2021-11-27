package com.vmenon.mpo.test

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
        if (resName.indexOf(".") != -1) {
            val resNameStart = resName.lastIndexOf(".")
            val packageName = resName.substring(0, resNameStart)
            val actualResName = resName.substring(resNameStart + 1, resName.length)
            clickOn(actualResName, packageName)
        } else {
            clickOn(resName)
        }
    }

    @When("I enter {string} into the {string} field")
    fun i_enter_into_field(input: String, resName: String) {
        if (resName.indexOf(".") != -1) {
            val resNameStart = resName.lastIndexOf(".")
            val packageName = resName.substring(0, resNameStart)
            val actualResName = resName.substring(resNameStart + 1, resName.length)
            text(input, actualResName, packageName)
        } else {
            text(input, resName)
        }
    }

    @Then("I should see {string} on the display")
    fun i_should_see_s_on_the_display(resName: String) {
        if (resName.indexOf(".") != -1) {
            val resNameStart = resName.lastIndexOf(".")
            val packageName = resName.substring(0, resNameStart)
            val actualResName = resName.substring(resNameStart + 1, resName.length)
            waitFor(actualResName, packageName)
        } else {
            waitFor(resName)
        }
    }
}
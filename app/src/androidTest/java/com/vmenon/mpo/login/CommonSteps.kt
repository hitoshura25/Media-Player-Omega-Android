package com.vmenon.mpo.login

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.vmenon.mpo.view.activity.HomeActivity
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import org.junit.Rule

class CommonSteps {
    @Rule
    private val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Given("I have launched the app")
    fun i_start_the_app() {

    }

    @When("I click on the Accounts tab")
    fun i_click_on_accounts_tab() {

    }
}
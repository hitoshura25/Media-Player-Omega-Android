package com.vmenon.mpo.test

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.vmenon.mpo.view.activity.HomeActivity
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.core.AllOf

class CommonSteps {
    lateinit var scenario: ActivityScenario<HomeActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(HomeActivity::class.java)
    }

    @After
    fun cleanup() {
        scenario.close()
    }

    @Given("I have launched the app")
    fun i_start_the_app() {

    }

    @When("I click on the {string} tab")
    fun i_click_on_tab(resName: String) {
        Espresso.onView(
            AllOf.allOf(
                ViewMatchers.withResourceName(resName),
                ViewMatchers.isDescendantOfA(ViewMatchers.withResourceName("navigation"))
            )
        ).perform(ViewActions.click())
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
        Espresso.onView(ViewMatchers.withResourceName(s)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}
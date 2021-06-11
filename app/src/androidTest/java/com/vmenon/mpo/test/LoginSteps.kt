package com.vmenon.mpo.test

import io.cucumber.java.en.Given

class LoginSteps : BaseSteps() {
    @Given("I have signed out of the app")
    fun i_have_signed_out_of_the_app() {
        clickOn(nav_accounts)
        clickOnIfVisible("logout_link")
        waitFor("login_link")
    }

    @Given("I have signed into the app with username {string} and password {string}")
    fun i_have_signed_in_with_username_password(username: String, password: String) {
        clickOn(nav_accounts)
        clickOn("login_link")
        waitForBrowser()
        browserText(username, "okta-signin-username")
        browserText(password, "okta-signin-password")
        browserClickOn("okta-signin-submit")
        waitForApp()
    }
}
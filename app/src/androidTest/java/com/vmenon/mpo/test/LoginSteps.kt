package com.vmenon.mpo.test

import io.cucumber.java.en.Given

class LoginSteps : BaseSteps() {
    @Given("I have signed out of the app")
    fun i_have_signed_out_of_the_app() {
        clickOn(nav_accounts)
        clickOnIfVisible("logout_link")
        waitFor("login_link")
    }

    @Given("I have launched sign in in the app")
    fun i_have_launched_sign_in() {
        clickOn(nav_accounts)
        clickOn("login_link")
    }

    @Given("I have completed sign in with username {string} and password {string}")
    fun i_have_signed_in_with_username_password(username: String, password: String) {
        waitForBrowser()
        browserText(username, "okta-signin-username")
        browserText(password, "okta-signin-password")
        browserClickOn("okta-signin-submit")
        waitForApp()
    }
}
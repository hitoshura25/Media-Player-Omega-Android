Feature: Register User Screen Shows Up
  Register a new user UI is visible when going to the Account tab and not logged in

  @smoke
  @e2e

  Scenario Outline: Register UI Displayed
    Given I have launched the app
    When I click on the "nav_account" tab
    And I click on "register_link"
    And I enter "<First Name>" into the "first_name" field
    And I enter "<Last Name>" into the "last_name" field
    And I enter "<E-mail>" into the "email" field
    And I enter "<Password>" into the "password" field
    And I enter "<Confirm Password>" into the "confirm_password" field
    And I click on "register_user"
    And I see a webpage with the "okta-signin-username" element
    And I enter "<E-mail>" into the "okta-signin-username" webpage element
    And I enter "<Password>" into the "okta-signin-password" webpage element
    And I click on the "okta-signin-submit" webpage element
    Then I should return to the app
    And I should see "settings" on the display
    Examples:
      | First Name | Last Name | E-mail | Password | Confirm Password |
      | Test    | Test     | test@test.com | IamGreat | IamGreat |
Feature: Login User Screen Shows Up
  Login UI is visible when going to the Account tab and not logged in

  @smoke
    @e2e

  Scenario Outline: Login UI Displayed
    Given I have launched the app
    When I click on the "nav_account" tab
    Then I should see "email_login" on the display
    And I should see "password_login" on the display
    Examples:
      |
      |
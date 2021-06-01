Feature: Register User Screen Shows Up
  Register a new user UI is visible when going to the Account tab and not logged in

  @smoke
  @e2e

  Scenario Outline: Register UI Displayed
    Given I have launched the app
    When I click on the Accounts tab
    Then I should see "<email>" and "<password>" on the display
    Examples:
      | email              | password |
      | vviswana@gmail.com | test123  |
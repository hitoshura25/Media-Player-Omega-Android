Feature: Login User Screen Shows Up
  Login UI is visible when going to the Account tab and not logged in

  @smoke
    @e2e

  Scenario Outline: Login UI Displayed
    Given I have launched the app
    When I click on the "nav_account" tab
    And I click on "login_link"
    And I see a webpage with the "okta-signin-username" element
    And I enter "<E-mail>" into the "okta-signin-username" webpage element
    And I enter "<Password>" into the "okta-signin-password" webpage element
    And I click on the "okta-signin-submit" webpage element
    Then I should return to the app
    And I should see "settings" on the display
    Examples:
      | E-mail | Password |
      | test2@test.com | IamGreat |


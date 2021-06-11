Feature: Login successful
  Login works

  @smoke
    @e2e

  Scenario Outline: Login UI Displayed
    Given I have launched the app
    And I have signed out of the app
    And I have signed into the app with username "<E-mail>" and password "<Password>"
    When I click on the "nav_account" tab
    Then I should see "settings" on the display
    Examples:
      | E-mail | Password |
      | test2@test.com | IamGreat |


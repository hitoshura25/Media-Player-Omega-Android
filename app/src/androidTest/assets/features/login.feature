Feature: Login successful
  Login works

  @smoke
    @e2e

  Scenario Outline: Valid login is successful
    Given I have launched the app
    And I have signed out of the app
    And I have launched sign in in the app
    And I have completed sign in with username "<E-mail>" and password "<Password>"
    When I click on "login_nav_graph"
    Then I should see "settings" on the display
    Examples:
      | E-mail | Password |
      | test2@test.com | IamGreat |


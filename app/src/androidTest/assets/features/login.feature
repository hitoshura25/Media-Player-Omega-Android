Feature: Login successful
  Login works

  @e2e
  @login_scenario_1
  Scenario Outline: Valid login is successful
    Given I have launched the app
    And I have signed out of the app
    And I have launched sign in in the app
    And I have completed sign in with username "<E-mail>" and password "<Password>"
    And I choose not to enroll in biometrics
    When I click on "login_nav_graph"
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    Examples:
      | E-mail | Password |
      | test2@test.com | IamGreat |

  @smoke
  @login_scenario_2
  Scenario Outline: Valid login is successful using mock authentication
    Given I have launched the app
    And The API responds to request "/user" with code 200 and body "user_details.json"
    And I have signed out of the app using mock authentication
    And I have launched sign in in the app using mock authentication
    And I choose not to enroll in biometrics
    When I click on "login_nav_graph"
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    Examples:
      |
      |



Feature: Login successful
  Login works

  @smoke
  Scenario Outline: Valid login is successful
    Given I have launched the app
    When I click on "login_nav_graph"
    And The dynamic feature module download completes
    And I click on "com.vmenon.mpo.login_feature.logout_link" if visible
    Then I should see "com.vmenon.mpo.login_feature.login_link" on the display
    Examples:
      |
      |

  @e2e
  @login_scenario_1
  Scenario Outline: Valid login is successful
    Given I have launched the app
    When I click on "login_nav_graph"
    And The dynamic feature module download completes
    And I click on "com.vmenon.mpo.login_feature.logout_link" if visible
    And I click on "com.vmenon.mpo.login_feature.login_link"
    And I wait for the browser to launch
    And I enter "<E-mail>" into the "okta-signin-username" field in the web browser
    And I enter "<Password>" into the "okta-signin-password" field in the web browser
    And I click on the "okta-signin-submit" field in the web browser
    And I choose not to enroll in biometrics
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    When I click on "com.vmenon.mpo.login_feature.logout_link"
    Then I should see "com.vmenon.mpo.login_feature.login_link" on the display
    Examples:
      | E-mail | Password |
      | test2@test.com | IamGreat |

  @regression
  @login_scenario_2
  Scenario Outline: Valid login is successful using mock authentication
    Given I have launched the app
    And I am using mock authentication
    And The API responds to request "/user" with code 200 and body "user_details.json"
    When I click on "login_nav_graph"
    And The dynamic feature module download completes
    And I click on "com.vmenon.mpo.login_feature.logout_link" if visible
    And I click on "com.vmenon.mpo.login_feature.login_link"
    And I choose not to enroll in biometrics
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    When I click on "com.vmenon.mpo.login_feature.logout_link"
    Then I should see "com.vmenon.mpo.login_feature.login_link" on the display
    Examples:
      |
      |

  @regression
  @login_scenario_3
  Scenario Outline: Valid login is successful using mock authentication and enroll in biometrics
    Given I have launched the app
    And I am using mock authentication
    And The API responds to request "/user" with code 200 and body "user_details.json"
    When I click on "login_nav_graph"
    And The dynamic feature module download completes
    And I click on "com.vmenon.mpo.login_feature.logout_link" if visible
    And I click on "com.vmenon.mpo.login_feature.login_link"
    And I choose to enroll in biometrics
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    When I click on "subscribed_shows_navigation_graph"
    And Biometrics is mocked with none enrolled
    And I need to reauth with biometrics
    Then I should see text containing "It appears your biometrics setup has changed" on the display
    When I choose to enroll in biometrics
    Then I should not see text containing "It appears your biometrics setup has changed" on the display
    When Biometrics is mocked with success
    And I need to reauth with biometrics
    Then I should not see text containing "It appears your biometrics setup has changed" on the display
    When I click on "login_nav_graph"
    And I click on "com.vmenon.mpo.login_feature.logout_link"
    Then I should see "com.vmenon.mpo.login_feature.login_link" on the display
    Examples:
      |
      |




Feature: Register User Successful
  Register a new user is successful

  @e2e
  @register_scenario_1
  Scenario Outline: Valid Registration is successful
    Given I have launched the app
    And I have signed out of the app
    When I click on "login_nav_graph"
    And I click on "com.vmenon.mpo.login_feature.register_link"
    And I enter "<First Name>" into the "com.vmenon.mpo.login_feature.first_name" field
    And I enter "<Last Name>" into the "com.vmenon.mpo.login_feature.last_name" field
    And I enter "<E-mail>" into the "com.vmenon.mpo.login_feature.email" field
    And I enter "<Password>" into the "com.vmenon.mpo.login_feature.password" field
    And I enter "<Confirm Password>" into the "com.vmenon.mpo.login_feature.confirm_password" field
    And I click on "com.vmenon.mpo.login_feature.register_user"
    And I have completed sign in with username "<E-mail>" and password "<Password>"
    And I choose not to enroll in biometrics
    Then I should see "com.vmenon.mpo.login_feature.settings" on the display
    Examples:
      | First Name | Last Name | E-mail | Password | Confirm Password |
      | Test    | Test     | test@test.com | IamGreat | IamGreat |
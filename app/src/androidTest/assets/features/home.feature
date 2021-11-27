Feature: Home tab is launched
  Subscribed shows are displayed when home tab is launched

  @smoke
  @e2e
  @home_scenario_1
  Scenario Outline: Subscribed Shows UI Displayed as Homepage
    Given I have launched the app
    When I click on "subscribed_shows_nav_graph"
    Then I should see "com.vmenon.mpo.my_library_feature.showList" on the display
    Examples:
      |
      |
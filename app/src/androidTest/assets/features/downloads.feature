Feature: Downloads tab is launched
  Downloads list is displayed when Downloads tab is launched

  @smoke
  @e2e
  @downloads_scenario_1
  Scenario Outline: Downloads UI Displayed when Downloads tab is launched
    Given I have launched the app
    When I click on "downloads_nav_graph"
    And The dynamic feature module download completes
    Then I should see "com.vmenon.mpo.downloads_feature.downloadsList" on the display
    Examples:
      |
      |
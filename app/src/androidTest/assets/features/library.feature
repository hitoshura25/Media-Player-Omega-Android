Feature: My Library tab is launched
  Library is displayed when Library tab is launched

  @smoke
  @e2e
  @library_scenario_1
  Scenario Outline: Library UI Displayed when Library tab is selected
    Given I have launched the app
    When I click on "my_library_nav_graph"
    Then I should see "com.vmenon.mpo.my_library_feature.libraryList" on the display
    Examples:
      |
      |
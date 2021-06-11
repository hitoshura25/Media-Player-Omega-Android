Feature: My Library tab is launched
  Library is displayed when Library tab is launched

  @smoke
    @e2e

  Scenario Outline: Library UI Displayed when Library tab is selected
    Given I have launched the app
    When I click on "nav_library"
    Then I should see "libraryList" on the display
    Examples:
      |
      |
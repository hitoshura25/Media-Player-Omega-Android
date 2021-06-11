Feature: Downloads tab is launched
  Downloads list is displayed when Downloads tab is launched

  @smoke
    @e2e

  Scenario Outline: Downloads UI Displayed when Downloads tab is launched
    Given I have launched the app
    When I click on "nav_downloads"
    Then I should see "downloadsList" on the display
    Examples:
      |
      |
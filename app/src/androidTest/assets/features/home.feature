Feature: Home tab is launched
  Subscribed shows are displayed when home tab is launched

  @smoke
    @e2e

  Scenario Outline: Subscribed Shows UI Displayed as Homepage
    Given I have launched the app
    When I click on the "nav_home" tab
    Then I should see "showList" on the display
    Examples:
      |
      |
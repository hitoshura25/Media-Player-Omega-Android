Feature: Home tab is launched
  Subscribed shows are displayed when home tab is launched

  @smoke
    @e2e

  Scenario Outline: Subscribed Shows UI Displayed as Homepage
    Given I have launched the app
    When I click on "subscribed_shows_nav_graph"
    Then I should see "showList" on the display
    Examples:
      |
      |
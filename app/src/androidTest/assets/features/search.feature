Feature: Search feature
  Search for shows as well as subscribe to one

  @smoke
  @e2e
  @search_scenario_1
  Scenario Outline: Search for shows from home page, view a show's details, and subscribe to it
    Given I have launched the app
    When I click on "subscribed_shows_nav_graph"
    And I click on "com.vmenon.mpo.my_library_feature.search"
    And I enter "<Keyword>" into the "com.vmenon.mpo.search_src_text" field
    And I press enter
    And I click on text "Game Scoop!"
    And I click on "com.vmenon.mpo.search_feature.fab"
    And I click on content description "Navigate up"
    And I click on content description "Navigate up"
    Then I should see content description "Game Scoop!" on the display
    Examples:
      | Keyword |
      | ign    |
Feature: Subscribe feature
  Search for shows, subscribe, and make sure download was launched

  @smoke
  @e2e
  @subscribe_feature
  @subscribe_scenario_search
  Scenario Outline: Search for shows from home page, view a show's details, and subscribe to it
    Given I have launched the app
    And The API responds to request "/podcasts?keyword=ign" with code 200 and body "search_response.json"
    And The API responds to request "/podcastdetails?feedUrl=http%3A%2F%2Ffeeds.ign.com%2Fignfeeds%2Fpodcasts%2Fgamescoop%2F&maxEpisodes=10" with code 200 and body "show_details.json"
    And The API responds to request "/podcastupdate?feedUrl=http%3A%2F%2Ffeeds.ign.com%2Fignfeeds%2Fpodcasts%2Fgamescoop%2F&publishTimestamp=0" with code 200 and body "show_update.json"
    When I click on "subscribed_shows_nav_graph"
    And The dynamic feature module download completes
    And I click on "com.vmenon.mpo.my_library_feature.search"
    And I enter "<Keyword>" into the "com.vmenon.mpo.search_src_text" field
    And I press enter
    And I click on text "Game Scoop!"
    And I click on "com.vmenon.mpo.search_feature.fab"
    Then I should see text "You have subscribed to this show" on the display
    Examples:
      | Keyword |
      | ign    |

  @smoke
  @e2e
  @subscribe_feature
  @subscribe_scenario_downloads
  Scenario Outline: Check download queued for subscribed show
    Given I have launched the app
    When I click on "downloads_nav_graph"
    And The dynamic feature module download completes
    Then I should see "com.vmenon.mpo.downloads_feature.downloadsList" on the display
    And I should see text "The 100 Questions Challenge (2021 Edition)" on the display
    When I wait for episode "The 100 Questions Challenge (2021 Edition)" to finish downloading
    Then I should not see text "The 100 Questions Challenge (2021 Edition)" on the display
    Examples:
      |
      |

  @smoke
  @e2e
  @subscribe_feature
  @subscribe_scenario_home
  Scenario Outline: Should see subscribed shows on Home
    Given I have launched the app
    When I click on "subscribed_shows_nav_graph"
    Then I should see "com.vmenon.mpo.my_library_feature.showList" on the display
    And I should see content description "Game Scoop!" on the display
    Examples:
      |
      |

  @smoke
  @e2e
  @subscribe_feature
  @subscribe_scenario_library
  Scenario Outline: Play episode from the library
    Given I have launched the app
    When I click on "my_library_nav_graph"
    Then I should see "com.vmenon.mpo.my_library_feature.libraryList" on the display
    When I click on text "The 100 Questions Challenge (2021 Edition)"
    And I click on "com.vmenon.mpo.my_library_feature.fab"
    And The dynamic feature module download completes
    Then I should see "com.vmenon.mpo.player_feature.actionButton" on the display
    When I click on "com.vmenon.mpo.player_feature.actionButton"
    Then I should see content description "Play Media" on the display
    When I click on "com.vmenon.mpo.player_feature.actionButton"
    Then I should see content description "Pause Media" on the display
    Examples:
      |
      |
package com.vmenon.mpo.api.pact

import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.PactProviderRule
import au.com.dius.pact.consumer.junit.PactVerification
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.vmenon.mpo.api.di.dagger.ApiModule
import com.vmenon.mpo.api.model.RegisterUserRequest
import com.vmenon.mpo.login.domain.AuthService
import io.pactfoundation.consumer.dsl.LambdaDsl
import io.pactfoundation.consumer.dsl.LambdaDslObject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MpoApiPactTest {

    @Rule
    @JvmField
    val provider = PactProviderRule("mpo-api", this)

    @Pact(consumer = "consumer-mpo-api")
    fun createSearchPact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("default")
            .uponReceiving("Search for shows")
            .path("/podcasts")
            .query("keyword=$SEARCH_KEYWORD")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(LambdaDsl.newJsonArray { array ->
                array.`object` { item ->
                    item.stringType(
                        "name",
                        "artworkUrl",
                        "feedUrl",
                        "smallArtworkUrl",
                        "author"
                    )
                    item.eachLike("genres", PactDslJsonRootValue.stringType())
                }
            }.build()).toPact()
    }

    @Pact(consumer = "consumer-mpo-api")
    fun createShowDetailsPact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("default")
            .uponReceiving("Get Show Details")
            .path("/podcastdetails")
            .query("maxEpisodes=$MAX_EPISODES&feedUrl=$FEED_URL")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(LambdaDsl.newJsonBody { root ->
                root.stringType(
                    "name",
                    "description",
                    "imageUrl"
                )
                root.eachLike("episodes") { item: LambdaDslObject ->
                    item.stringType(
                        "name",
                        "artworkUrl",
                        "feedUrl",
                        "smallArtworkUrl",
                        "author"
                    )
                    item.eachLike("genres", PactDslJsonRootValue.stringType())
                }
            }.build()).toPact()
    }

    @Pact(consumer = "consumer-mpo-api")
    fun createShowUpdatePact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("default")
            .uponReceiving("Get Show update")
            .path("/podcastupdate")
            .query("feedUrl=$FEED_URL&publishTimestamp=$PUBLISH_TIMESTAMP")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(LambdaDsl.newJsonBody { root ->
                root.stringType(
                    "name",
                    "artworkUrl",
                    "description",
                    "type",
                    "artworkUrl"
                )
                root.numberType(
                    "published",
                    "length"
                )
            }.build()).toPact()
    }

    @Pact(consumer = "consumer-mpo-api")
    fun registerUserPact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("default")
            .uponReceiving("Register a User")
            .path("/register_user")
            .method("POST")
            .body(LambdaDsl.newJsonBody { body ->
                body.stringType(
                    "firstName",
                    "lastName",
                    "email",
                    "password"
                )
            }.build())
            .willRespondWith()
            .status(200)
            .body(LambdaDsl.newJsonBody { body ->
                body.stringType(
                    "firstName",
                    "lastName",
                    "email"
                )
            }.build()).toPact()
    }

    @Test
    @PactVerification(fragment = "createSearchPact")
    fun `should fetch shows from provider`() {
        val httpClient = ApiModule.provideHttpClient(Mockito.mock(AuthService::class.java))
        val client = ApiModule.provideMediaPlayerRetrofitApi(provider.url, httpClient)
        val response = client.searchPodcasts(SEARCH_KEYWORD).blockingGet()
        Assert.assertFalse(response.isEmpty())
    }

    @Test
    @PactVerification(fragment = "createShowDetailsPact")
    fun `should fetch show details from provider`() {
        val httpClient = ApiModule.provideHttpClient(Mockito.mock(AuthService::class.java))
        val client = ApiModule.provideMediaPlayerRetrofitApi(provider.url, httpClient)
        val response = client.getPodcastDetails(FEED_URL, MAX_EPISODES).blockingGet()
        Assert.assertNotNull(response)
    }

    @Test
    @PactVerification(fragment = "createShowUpdatePact")
    fun `should fetch show update from provider`() {
        val httpClient = ApiModule.provideHttpClient(Mockito.mock(AuthService::class.java))
        val client = ApiModule.provideMediaPlayerRetrofitApi(provider.url, httpClient)
        val response = client.getPodcastUpdate(FEED_URL, PUBLISH_TIMESTAMP).blockingGet()
        Assert.assertNotNull(response)
    }

    @Test
    @PactVerification(fragment = "registerUserPact")
    fun `should register a User`() {
        val httpClient = ApiModule.provideHttpClient(Mockito.mock(AuthService::class.java))
        val client = ApiModule.provideMediaPlayerRetrofitApi(provider.url, httpClient)
        val response = client.registerUser(
            RegisterUserRequest(
                firstName = "User First Name",
                lastName = "User Last Name",
                email = "User email",
                password = "User password"
            )
        ).blockingGet()
        Assert.assertNotNull(response)
    }

    companion object {
        private const val SEARCH_KEYWORD = "Game Scoop! TV (Video)"
        private const val MAX_EPISODES = 10
        private const val FEED_URL = "http://feeds.ign.com/ignfeeds/podcasts/video/gamescoop"
        private const val PUBLISH_TIMESTAMP = 1507661400000
    }
}
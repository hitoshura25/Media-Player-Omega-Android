package com.vmenon.mpo.api.pact

import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.PactProviderRule
import au.com.dius.pact.consumer.junit.PactVerification
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.vmenon.mpo.api.di.dagger.ApiModule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class PactTest {
    @Rule
    @JvmField
    val provider = PactProviderRule("mpo-api", this)

    @Pact(consumer = "consumer-mpo-api")
    fun createPact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("default")
            .uponReceiving("Search for shows")
            .path("/podcasts")
            .query("keyword=Game Scoop! TV (Video)")
            .method("GET")
            /*.headers(mapOf(
                "User-Agent" to "DoorDashConsumer/Android"
            ))*/
            .willRespondWith()
            .status(200)
            .body(this::class.java.getResource("/responses/search_response.json").readText())
            .toPact()
    }

    @Test
    @PactVerification
    fun `should fetch shows from provider`() {
        val client = ApiModule.provideMediaPlayerRetrofitApi(provider.url)
        val response = client.searchPodcasts("Game Scoop! TV (Video)").blockingGet()

        Assert.assertFalse(response.isEmpty())
    }
}
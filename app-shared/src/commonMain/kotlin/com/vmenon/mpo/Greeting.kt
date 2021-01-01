package com.vmenon.mpo

import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.ShowModel

class Greeting {
    fun greeting(): String {
        val result = SuccessState(
            ShowModel(
                name = "The Show",
                genres = emptyList(),
                artworkUrl = null,
                author = "",
                description = "",
                feedUrl = "",
                id = 1L,
                isSubscribed = false,
                lastEpisodePublished = 0L,
                lastUpdate = 0L
            )
        )
        return "Hello, ${result.result.name} on ${Platform().platform}!"
    }
}
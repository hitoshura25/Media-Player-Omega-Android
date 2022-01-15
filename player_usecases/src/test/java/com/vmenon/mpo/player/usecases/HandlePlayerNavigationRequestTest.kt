package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.navigation.domain.player.FileMediaSource
import com.vmenon.mpo.navigation.domain.player.Media
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams
import com.vmenon.mpo.player.domain.NavigationParamsConverter
import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HandlePlayerNavigationRequestTest {
    private val converter: NavigationParamsConverter = mock()

    @Test
    fun handlePlayerNavigationRequest() = runBlockingTest {
        val useCase = HandlePlayerNavigationRequest(converter)
        val params = PlayerNavigationParams(
            media = Media(
                mediaId = "mediaId",
                mediaSource = FileMediaSource("file")
            )
        )
        whenever(converter.createPlaybackMediaRequest(params)).thenReturn(TestData.playbackMediaRequest)
        assertEquals(TestData.playbackMediaRequest, useCase.invoke(params))
    }
}
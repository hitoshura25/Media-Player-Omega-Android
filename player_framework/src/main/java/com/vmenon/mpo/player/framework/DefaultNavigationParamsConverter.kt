package com.vmenon.mpo.player.framework

import android.media.MediaMetadataRetriever
import com.vmenon.mpo.extensions.useFileDescriptor
import com.vmenon.mpo.navigation.domain.player.FileMediaSource
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.NavigationParamsConverter
import com.vmenon.mpo.player.domain.PlaybackMedia
import java.io.File
import java.lang.IllegalArgumentException

class DefaultNavigationParamsConverter : NavigationParamsConverter {
    override suspend fun createPlaybackMediaRequest(
        params: PlayerNavigationParams
    ): PlaybackMediaRequest? {
        return params.media?.let { media ->
            when (val mediaSource = media.mediaSource) {
                is FileMediaSource -> {
                    val mediaFile = File(mediaSource.mediaFile)
                    PlaybackMediaRequest(
                        PlaybackMedia(
                            mediaId = media.mediaId,
                            author = media.author,
                            album = media.album,
                            title = media.title,
                            artworkUrl = media.artworkUrl ?: media.artworkUrl,
                            genres = media.genres,
                            durationInMillis = getDuration(mediaFile)
                        ),
                        mediaSource.mediaFile
                    )
                }
                else -> throw IllegalArgumentException(
                    "Unsupported MediaSource ${mediaSource::class.java.name}"
                )
            }
        }
    }

    private fun getDuration(mediaFile: File): Long {
        val retriever = MediaMetadataRetriever()
        mediaFile.useFileDescriptor { fileDescriptor ->
            retriever.setDataSource(fileDescriptor)
        }

        return retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION
        )?.toLong() ?: 0L
    }
}
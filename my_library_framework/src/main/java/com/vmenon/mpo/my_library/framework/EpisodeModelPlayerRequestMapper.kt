package com.vmenon.mpo.my_library.framework

import android.media.MediaMetadataRetriever
import com.vmenon.mpo.extensions.useFileDescriptor
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import java.io.File

class EpisodeModelPlayerRequestMapper : PlayerRequestMapper<EpisodeModel> {
    override fun createMediaId(item: EpisodeModel): PlaybackMediaRequest =
        item.filename?.let { filename ->
            val mediaFile = File(filename)
            val duration = item.lengthInSeconds?.let { length ->
                length * 1000
            } ?: getDuration(mediaFile)
            PlaybackMediaRequest(
                PlaybackMedia(
                    mediaId = "episode:${item.id}",
                    author = item.show.author,
                    album = item.show.name,
                    title = item.name,
                    artworkUrl = item.artworkUrl ?: item.show.artworkUrl,
                    genres = item.show.genres,
                    durationInMillis = duration
                ),
                filename
            )
        } ?: throw IllegalStateException("Filename cannot be null!")

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
package com.vmenon.mpo.util

import com.vmenon.mpo.api.Episode

object MediaHelper {

    const val MEDIA_TYPE_EPISODE = 0

    private const val EPISODE_MEDIA_PREFIX = "episode"

    class MediaType(mediaType: Int, id: Long) {
        var mediaType: Int = 0
            internal set
        var id: Long = 0
            internal set

        init {
            this.mediaType = mediaType
            this.id = id
        }
    }

    fun createMediaId(episode: Episode): String {
        return EPISODE_MEDIA_PREFIX + ":" + episode.id
    }

    fun getMediaTypeFromMediaId(mediaId: String): MediaType? {
        val mediaIdParts = mediaId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (EPISODE_MEDIA_PREFIX == mediaIdParts[0]) {
            val episodeId = java.lang.Long.parseLong(mediaIdParts[1])
            return MediaType(MEDIA_TYPE_EPISODE, episodeId)
        }

        return null
    }
}

package com.vmenon.mpo.player.domain

data class PlaybackState(
    val media: PlaybackMedia,
    val duration: Long,
    val positionInMillis: Long,
    val state: State
)

data class PlaybackMedia(
    val mediaId: String,
    val title: String? = null,
    val author: String? = null,
    val artworkUrl: String? = null,
    val album: String? = null,
    val genres: List<String>? = null
)

enum class State {
    PLAYING,
    BUFFERING,
    STOPPED,
    PAUSED,
    FAST_FORWARDING,
    REWINDING,
    ERROR,
    NONE,
    UNKNOWN
}
package com.vmenon.mpo.player.domain

data class PlaybackState(
    val media: PlaybackMedia,
    val durationInMillis: Long,
    val positionInMillis: Long,
    val state: State
) {
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
}
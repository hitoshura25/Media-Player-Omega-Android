package com.vmenon.mpo.player.domain

interface PlayerEngine {
    suspend fun connectClient(playerClient: PlayerClient): Boolean
    suspend fun play(mediaId: String)
    suspend fun resume()
    suspend fun pause()
    suspend fun stop()
    suspend fun seekTo(position: Long)
    suspend fun disconnectClient(playerClient: PlayerClient)
    suspend fun getCurrentPlaybackState(): PlaybackState?
}
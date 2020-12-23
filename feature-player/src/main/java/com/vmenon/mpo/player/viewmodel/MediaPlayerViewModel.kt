package com.vmenon.mpo.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.player.domain.PlayerClient
import com.vmenon.mpo.player.usecases.PlayerInteractors
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaPlayerViewModel : ViewModel() {
    @Inject
    lateinit var playerInteractors: PlayerInteractors

    val playBackState = liveData<PlaybackState> {
        emitSource(playerInteractors.listenForPlayBackStateChanges().asLiveData())
    }

    fun connectClient(client: PlayerClient) = liveData {
        emit(playerInteractors.connectPlayerClient(client))
    }

    fun disconnectClient(client: PlayerClient) {
        viewModelScope.launch {
            playerInteractors.disconnectPlayerClient(client)
        }
    }

    fun togglePlaybackState(requestedMedia: String?) {
        viewModelScope.launch {
            playerInteractors.togglePlaybackState(requestedMedia)
        }
    }

    fun skipPlayback(amount: Long) {
        viewModelScope.launch {
            playerInteractors.skipPlayback(amount)
        }
    }

    fun playMedia(requestedMedia: String) {
        viewModelScope.launch {
            playerInteractors.playMedia(requestedMedia)
        }
    }

    fun seekToPosition(position: Long) {
        viewModelScope.launch {
            playerInteractors.seekToPosition(position)
        }
    }
}
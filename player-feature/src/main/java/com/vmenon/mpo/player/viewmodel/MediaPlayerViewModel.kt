package com.vmenon.mpo.player.viewmodel

import androidx.lifecycle.*
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

    val connected: LiveData<Unit> = MutableLiveData()

    fun connectClient(client: PlayerClient) {
        viewModelScope.launch {
            if (playerInteractors.connectPlayerClient(client)) {
                (connected as MutableLiveData).postValue(Unit)
            }
        }
    }

    fun disconnectClient(client: PlayerClient) {
        viewModelScope.launch {
            playerInteractors.disconnectPlayerClient(client)
        }
    }

    fun togglePlaybackState() {
        viewModelScope.launch {
            playerInteractors.togglePlaybackState()
        }
    }

    fun skipPlayback(amountSeconds: Long) {
        viewModelScope.launch {
            playerInteractors.skipPlayback(amountSeconds * 1000)
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
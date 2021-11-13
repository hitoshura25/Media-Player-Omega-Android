package com.vmenon.mpo.my_library.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeDetailsViewModel : ViewModel() {

    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    fun getEpisodeDetails(id: Long) = liveData<ResultState<EpisodeModel>> {
        emitSource(myLibraryInteractors.getEpisodeDetails(id).asLiveData())
    }

    fun playEpisode(id: Long, origin: NavigationOrigin<*>) {
        viewModelScope.launch {
            myLibraryInteractors.playEpisode(id, origin)
        }
    }
}
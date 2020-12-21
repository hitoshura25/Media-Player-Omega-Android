package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import javax.inject.Inject

class EpisodeDetailsViewModel : ViewModel() {

    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    fun getEpisodeDetails(id: Long) = liveData<ResultState<EpisodeModel>> {
        emitSource(myLibraryInteractors.getEpisodeDetails(id).asLiveData())
    }
}
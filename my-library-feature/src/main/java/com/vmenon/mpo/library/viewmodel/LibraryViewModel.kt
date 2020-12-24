package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import javax.inject.Inject

class LibraryViewModel : ViewModel() {
    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    fun allEpisodes() = liveData<ResultState<List<EpisodeModel>>> {
        emitSource(
            myLibraryInteractors.getAllEpisodes().asLiveData()
        )
    }
}
package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import javax.inject.Inject

class SubscribedShowsViewModel : ViewModel() {
    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    val subscribedShows: LiveData<ResultState<List<ShowModel>>> = liveData {
        emitSource(myLibraryInteractors.getSubscribedShows().asLiveData())
    }
}
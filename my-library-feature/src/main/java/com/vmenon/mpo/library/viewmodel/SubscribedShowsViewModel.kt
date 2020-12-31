package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscribedShowsViewModel : ViewModel() {
    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    fun searchForShows(query: String, origin: NavigationOrigin<*>) {
        viewModelScope.launch {
            myLibraryInteractors.searchForShows(query, origin)
        }
    }

    fun subscribedShows(): LiveData<ResultState<List<ShowModel>>> = liveData {
        emitSource(myLibraryInteractors.getSubscribedShows().asLiveData())
    }
}
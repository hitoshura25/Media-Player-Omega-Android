package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.MyLibraryService
import kotlinx.coroutines.flow.flow

class GetSubscribedShows(private val myLibraryService: MyLibraryService) {
    suspend operator fun invoke() = flow {
        emit(LoadingState)
        emit(SuccessState(myLibraryService.getAllSubscribedShows()))
    }
}
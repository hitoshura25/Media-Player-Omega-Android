package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetEpisodeDetails(private val myLibraryService: MyLibraryService) {
    suspend operator fun invoke(episodeId: Long): Flow<ResultState<EpisodeModel>> = flow {
        emit(LoadingState)
        emit(SuccessState(myLibraryService.getEpisode(episodeId)))
    }
}
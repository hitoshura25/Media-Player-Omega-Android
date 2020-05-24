package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun getSubscribed(): Flow<List<ShowModel>>
    suspend fun save(show: ShowModel): ShowModel
    suspend fun getSubscribedAndLastUpdatedBefore(interval: Long): List<ShowModel>?
    suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel?
}
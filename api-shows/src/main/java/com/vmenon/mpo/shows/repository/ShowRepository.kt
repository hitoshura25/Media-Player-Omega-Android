package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel

interface ShowRepository {
    suspend fun getSubscribed(): List<ShowModel>
    suspend fun save(show: ShowModel): ShowModel
    suspend fun getSubscribedAndLastUpdatedBefore(interval: Long): List<ShowModel>?
    suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel?
}
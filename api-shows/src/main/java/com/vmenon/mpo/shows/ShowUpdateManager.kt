package com.vmenon.mpo.shows

import com.vmenon.mpo.model.ShowModel

interface ShowUpdateManager {
    suspend fun updateAllShows()
    suspend fun updateShow(show: ShowModel)
}
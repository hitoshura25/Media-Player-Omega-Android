package com.vmenon.mpo.shows

import com.vmenon.mpo.model.ShowModel
import io.reactivex.Completable

interface ShowUpdateManager {
    fun updateAllShows(): Completable
    fun updateShow(show: ShowModel): Completable
}
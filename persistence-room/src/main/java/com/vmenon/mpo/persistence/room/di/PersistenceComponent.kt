package com.vmenon.mpo.persistence.room.di

import android.app.Application
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import dagger.BindsInstance
import dagger.Component

@Component(modules = [RoomModule::class])
@PersistenceScope
interface PersistenceComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): PersistenceComponent
    }

    fun showSearchResultsDao(): ShowSearchResultDao
    fun showDao(): ShowDao
    fun episodeDao(): EpisodeDao
    fun downloadDao(): DownloadDao
}
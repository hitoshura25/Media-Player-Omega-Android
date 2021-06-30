package com.vmenon.mpo.persistence.di.dagger

import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

// TODO May be able to just get rid of this and use the persistence module directly in
// common_framework
@Component(
    dependencies = [SystemFrameworkComponent::class],
    modules = [PersistenceModule::class]
)
@PersistenceScope
interface PersistenceComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun build(): PersistenceComponent
    }

    fun downloadDao(): DownloadDao
    fun showDao(): ShowDao
    fun episodeDao(): EpisodeDao
    fun showSearchResultsDao(): ShowSearchResultDao
}
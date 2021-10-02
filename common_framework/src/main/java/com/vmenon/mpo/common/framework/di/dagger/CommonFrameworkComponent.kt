package com.vmenon.mpo.common.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.system.domain.ThreadUtil
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(
    dependencies = [
        AuthComponent::class,
        PersistenceComponent::class,
        SystemFrameworkComponent::class,
        NavigationFrameworkComponent::class
    ],
    modules = [ApiModule::class]
)
@CommonFrameworkScope
interface CommonFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun authComponent(component: AuthComponent): Builder
        fun persistenceComponent(component: PersistenceComponent): Builder
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun navigationFrameworkComponent(component: NavigationFrameworkComponent): Builder
        fun build(): CommonFrameworkComponent
    }

    fun application(): Application
    fun logger(): Logger
    fun clock(): Clock
    fun threadUtil(): ThreadUtil
    fun authService(): AuthService
    fun api(): MediaPlayerOmegaRetrofitService

    fun downloadDao(): DownloadDao
    fun episodeDao(): EpisodeDao
    fun showDao(): ShowDao
    fun showSearchResultDao(): ShowSearchResultDao

    fun searchNavigationDestination(): NavigationDestination<SearchNavigationLocation>
    fun playerNavigationDestination(): NavigationDestination<PlayerNavigationLocation>
    fun navigationController(): NavigationController
    fun biometricsManager(): BiometricsManager
}
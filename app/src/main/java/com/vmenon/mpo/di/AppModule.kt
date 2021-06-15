package com.vmenon.mpo.di

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.HomeNavigationParams
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.core.SystemImpl

import com.vmenon.mpo.core.navigation.DefaultNavigationController
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.view.activity.HomeActivity

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Provides
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    fun providesNavigationController(
        shows: NavigationDestination<SubscribedShowsLocation>,
        library: NavigationDestination<MyLibraryNavigationLocation>,
        account: NavigationDestination<LoginNavigationLocation>,
        downloads: NavigationDestination<DownloadsLocation>,
        system: System
    ): NavigationController = DefaultNavigationController(
        mapOf(
            Pair(com.vmenon.mpo.R.id.nav_home, shows),
            Pair(com.vmenon.mpo.R.id.nav_library, library),
            Pair(com.vmenon.mpo.R.id.nav_account, account),
            Pair(com.vmenon.mpo.R.id.nav_downloads, downloads)
        ),
        system
    )

    @Provides
    fun provideHomeDestination(): NavigationDestination<HomeLocation> =
        ActivityDestination(
            activityClass = HomeActivity::class.java,
            location = HomeLocation
        )

    @Provides
    fun providesMPOMediaBrowserServiceConfiguration(
        application: Application,
        player: MPOPlayer,
        homeDestination: NavigationDestination<HomeLocation>
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        player,
        { request: PlaybackMediaRequest?, context: Context ->
            (homeDestination as ActivityDestination<HomeLocation>).createIntent(
                application,
                DefaultNavigationController.NAVIGATION_PARAMS_NAME,
                HomeNavigationParams(request)
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(application, R.color.colorPrimary)
        })

    @Provides
    fun provideSystem(): System = SystemImpl()
}

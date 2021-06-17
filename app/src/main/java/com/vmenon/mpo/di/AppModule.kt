package com.vmenon.mpo.di

import android.app.Application
import com.vmenon.mpo.HomeLocation
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
import com.vmenon.mpo.view.activity.HomeActivity

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Provides
    fun providesApplication(): Application = application

    @Provides
    @AppScope
    fun providesNavigationController(
        shows: NavigationDestination<SubscribedShowsLocation>,
        library: NavigationDestination<MyLibraryNavigationLocation>,
        account: NavigationDestination<LoginNavigationLocation>,
        downloads: NavigationDestination<DownloadsLocation>,
        system: System
    ): NavigationController = DefaultNavigationController(
        mapOf(
            Pair(com.vmenon.mpo.R.id.subscribed_shows_nav_graph, shows),
            Pair(com.vmenon.mpo.R.id.my_library_nav_graph, library),
            Pair(com.vmenon.mpo.R.id.login_nav_graph, account),
            Pair(com.vmenon.mpo.R.id.downloads_nav_graph, downloads)
        ),
        system
    )

    @Provides
    fun provideHomeDestination(): NavigationDestination<HomeLocation> =
        ActivityDestination(
            activityClass = HomeActivity::class.java,
            location = HomeLocation
        )

    /*@Provides
    fun providesMPOMediaBrowserServiceConfiguration(
        application: Application,
        player: MPOPlayer,
        playerDestination: NavigationDestination<PlayerNavigationLocation>,
        navigationController: NavigationController
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        player,
        { request: PlaybackMediaRequest?, context: Context ->
            navigationController.createNavigationRequest(
                context,
                PlayerNavigationParams(request),
                playerDestination
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(application, R.color.colorPrimary)
        })*/

    @Provides
    fun provideSystem(): System = SystemImpl()
}

package com.vmenon.mpo.di

import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.R
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.navigation.framework.DefaultNavigationController
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

@Module
class AppModule {

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
}

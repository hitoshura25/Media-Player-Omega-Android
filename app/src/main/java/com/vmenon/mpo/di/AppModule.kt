package com.vmenon.mpo.di

import android.app.Application
import androidx.core.content.ContextCompat
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.HomeNavigationParams
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.core.SystemImpl

import com.vmenon.mpo.core.navigation.DefaultNavigationController
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
    fun providesNavigationController(): NavigationController = DefaultNavigationController()

    @Provides
    fun provideHomeDestination(): NavigationDestination<HomeLocation> =
        ActivityDestination(
            activityClass = HomeActivity::class.java
        )

    @Provides
    fun providesMPOMediaBrowserServiceConfiguration(
        application: Application,
        player: MPOPlayer,
        homeDestination: NavigationDestination<HomeLocation>
    ): MPOMediaBrowserService.Configuration = MPOMediaBrowserService.Configuration(
        player,
        { request: PlaybackMediaRequest? ->
            (homeDestination as ActivityDestination<HomeLocation>).createIntent(
                application,
                HomeNavigationParams(request)
            )
        },
        { builder ->
            builder.color = ContextCompat.getColor(application, R.color.colorPrimary)
        })

    @Provides
    fun provideSystem(): System = SystemImpl()
}

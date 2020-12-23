package com.vmenon.mpo.di

import android.app.Application

import com.vmenon.mpo.core.navigation.DefaultNavigationController
import com.vmenon.mpo.navigation.domain.NavigationController

import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {
    @Provides
    fun providesApplication(): Application = application

    @Provides
    fun providesNavigationController(): NavigationController = DefaultNavigationController()
}

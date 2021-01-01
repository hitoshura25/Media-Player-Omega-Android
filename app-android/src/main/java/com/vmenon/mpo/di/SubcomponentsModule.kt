package com.vmenon.mpo.di

import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.player.di.dagger.PlayerComponent
import com.vmenon.mpo.search.di.dagger.SearchComponent
import dagger.Module

@Module(subcomponents = [
    ActivityComponent::class,
    SearchComponent::class,
    DownloadsComponent::class,
    PlayerComponent::class
])
class SubcomponentsModule
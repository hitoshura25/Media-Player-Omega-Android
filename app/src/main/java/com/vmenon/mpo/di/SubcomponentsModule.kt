package com.vmenon.mpo.di

import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.search.di.dagger.SearchComponent
import dagger.Module

@Module(subcomponents = [
    ActivityComponent::class,
    SearchComponent::class,
    DownloadsComponent::class
])
class SubcomponentsModule
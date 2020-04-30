package com.vmenon.mpo.di

import com.vmenon.mpo.search.di.dagger.SearchComponent
import dagger.Module

@Module(subcomponents = [ActivityComponent::class, SearchComponent::class])
class SubcomponentsModule
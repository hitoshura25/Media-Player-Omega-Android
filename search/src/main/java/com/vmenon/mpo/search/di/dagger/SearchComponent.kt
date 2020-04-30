package com.vmenon.mpo.search.di.dagger

import dagger.Subcomponent

@Subcomponent(modules = [SearchModule::class])
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }
}
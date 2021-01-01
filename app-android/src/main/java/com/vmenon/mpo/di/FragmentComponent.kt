package com.vmenon.mpo.di

import dagger.Subcomponent

@Subcomponent
@FragmentScope
interface FragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): FragmentComponent
    }
}
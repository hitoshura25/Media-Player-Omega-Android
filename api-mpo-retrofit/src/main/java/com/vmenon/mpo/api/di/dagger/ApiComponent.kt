package com.vmenon.mpo.api.di.dagger

import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    @Component.Builder
    interface Builder {
        fun build(): ApiComponent
    }
}
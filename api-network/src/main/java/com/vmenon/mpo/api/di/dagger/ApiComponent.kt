package com.vmenon.mpo.api.di.dagger

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    @Component.Builder
    interface Builder {
        fun build(): ApiComponent
    }
    fun api(): MediaPlayerOmegaApi
}
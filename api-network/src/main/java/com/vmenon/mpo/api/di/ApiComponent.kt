package com.vmenon.mpo.api.di

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import dagger.Component

@Component(modules = [ApiModule::class])
@ApiScope
interface ApiComponent {
    @Component.Builder
    interface Builder {
        fun build(): ApiComponent
    }

    fun api(): MediaPlayerOmegaApi
}
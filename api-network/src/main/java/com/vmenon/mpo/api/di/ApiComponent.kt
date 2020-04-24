package com.vmenon.mpo.api.di

import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaService
import dagger.Component

@Component(modules = [ApiModule::class])
@ApiScope
interface ApiComponent {
    fun service(): MediaPlayerOmegaService
}
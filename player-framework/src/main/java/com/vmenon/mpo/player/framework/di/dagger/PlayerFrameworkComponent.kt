package com.vmenon.mpo.player.framework.di.dagger

import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import dagger.Subcomponent

@Subcomponent
interface PlayerFrameworkComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PlayerFrameworkComponent
    }
    fun inject(service: MPOMediaBrowserService)

}
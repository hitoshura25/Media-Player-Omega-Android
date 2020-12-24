package com.vmenon.mpo.di

import com.vmenon.mpo.view.activity.*
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(activity: HomeActivity)
}
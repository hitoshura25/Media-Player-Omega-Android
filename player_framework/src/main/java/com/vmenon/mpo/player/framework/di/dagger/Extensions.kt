package com.vmenon.mpo.player.framework.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.di.AppComponentProvider

fun Context.toPlayerFrameworkComponent(): PlayerFrameworkComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val appComponent = (applicationContext as AppComponentProvider).appComponent()
    return DaggerPlayerFrameworkComponent.builder()
        .appComponent(appComponent)
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
}
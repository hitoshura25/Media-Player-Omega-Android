package com.vmenon.mpo.player.framework.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider

fun Context.toPlayerFrameworkComponent(): PlayerFrameworkComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    return DaggerPlayerFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
}
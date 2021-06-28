package com.vmenon.mpo.player.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.player.framework.di.dagger.DaggerPlayerFrameworkComponent

fun Context.toPlayerComponent(): PlayerComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val frameworkComponent = DaggerPlayerFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
    return DaggerPlayerComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .playerFrameworkComponent(frameworkComponent)
        .build()
}
package com.vmenon.mpo.player.presentation.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponentProvider

fun Context.toPlayerComponent(): PlayerComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val frameworkComponent =
        (applicationContext as PlayerFrameworkComponentProvider).playerFrameworkComponent()
    return DaggerPlayerComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .playerFrameworkComponent(frameworkComponent)
        .build()
}
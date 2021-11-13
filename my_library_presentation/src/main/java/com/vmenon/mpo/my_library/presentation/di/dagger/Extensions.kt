package com.vmenon.mpo.my_library.presentation.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.my_library.framework.di.dagger.DaggerLibraryFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponentProvider

fun Context.toLibraryComponent(): LibraryComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val libraryFrameworkComponent = DaggerLibraryFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
    val playerFrameworkComponent =
        (applicationContext as PlayerFrameworkComponentProvider).playerFrameworkComponent()

    return DaggerLibraryComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .libraryFrameworkComponent(libraryFrameworkComponent)
        .playerFrameworkComponent(playerFrameworkComponent)
        .build()
}
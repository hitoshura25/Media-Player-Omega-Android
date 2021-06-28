package com.vmenon.mpo.library.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.my_library.framework.di.dagger.DaggerLibraryFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.DaggerPlayerFrameworkComponent

fun Context.toLibraryComponent(): LibraryComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val libraryFrameworkComponent = DaggerLibraryFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
    val playerFrameworkComponent = DaggerPlayerFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    return DaggerLibraryComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .libraryFrameworkComponent(libraryFrameworkComponent)
        .playerFrameworkComponent(playerFrameworkComponent)
        .build()
}
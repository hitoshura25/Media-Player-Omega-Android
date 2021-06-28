package com.vmenon.mpo.downloads.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.downloads.framework.di.dagger.DaggerDownloadsFrameworkComponent
import com.vmenon.mpo.my_library.framework.di.dagger.DaggerLibraryFrameworkComponent

fun Context.toDownloadsComponent(): DownloadsComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()

    val downloadsFrameworkComponent = DaggerDownloadsFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    val libraryFrameworkComponent = DaggerLibraryFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    return DaggerDownloadsComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .downloadsFrameworkComponent(downloadsFrameworkComponent)
        .libraryFrameworkComponent(libraryFrameworkComponent)
        .build()
}
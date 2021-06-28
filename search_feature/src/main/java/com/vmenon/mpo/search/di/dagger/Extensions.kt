package com.vmenon.mpo.search.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.downloads.framework.di.dagger.DaggerDownloadsFrameworkComponent
import com.vmenon.mpo.my_library.framework.di.dagger.DaggerLibraryFrameworkComponent
import com.vmenon.mpo.search.framework.di.dagger.DaggerSearchFrameworkComponent

fun Context.toSearchComponent(): SearchComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val searchFrameworkComponent = DaggerSearchFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
    val downloadsFrameworkComponent = DaggerDownloadsFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()
    val libraryFrameworkComponent = DaggerLibraryFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    return DaggerSearchComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .downloadsFrameworkComponent(downloadsFrameworkComponent)
        .libraryFrameworkComponent(libraryFrameworkComponent)
        .searchFrameworkComponent(searchFrameworkComponent)
        .build()
}
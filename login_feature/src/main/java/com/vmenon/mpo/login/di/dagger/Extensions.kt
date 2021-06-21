package com.vmenon.mpo.login.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.di.AppComponentProvider
import com.vmenon.mpo.login.di.dagger.DaggerLoginComponent
import com.vmenon.mpo.login.framework.di.DaggerLoginFrameworkComponent

fun Context.toLoginComponent(): LoginComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val appComponent = (applicationContext as AppComponentProvider).appComponent()
    val loginFrameworkComponent = DaggerLoginFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    return DaggerLoginComponent.builder()
        .appComponent(appComponent)
        .commonFrameworkComponent(commonFrameworkComponent)
        .loginFrameworkComponent(loginFrameworkComponent)
        .build()
}
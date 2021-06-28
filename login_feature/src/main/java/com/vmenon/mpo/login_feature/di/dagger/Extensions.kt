package com.vmenon.mpo.login_feature.di.dagger

import android.content.Context
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.login.framework.di.DaggerLoginFrameworkComponent

fun Context.toLoginComponent(): LoginComponent {
    val commonFrameworkComponent =
        (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()
    val loginFrameworkComponent = DaggerLoginFrameworkComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .build()

    return DaggerLoginComponent.builder()
        .commonFrameworkComponent(commonFrameworkComponent)
        .loginFrameworkComponent(loginFrameworkComponent)
        .build()
}
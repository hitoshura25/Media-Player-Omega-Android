package com.vmenon.mpo.common.framework.di.dagger

import android.content.Context

fun Context.toCommonFrameworkComponent(): CommonFrameworkComponent =
    (applicationContext as CommonFrameworkComponentProvider).commonFrameworkComponent()

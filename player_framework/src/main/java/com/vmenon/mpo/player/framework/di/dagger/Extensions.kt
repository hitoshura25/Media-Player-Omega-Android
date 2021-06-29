package com.vmenon.mpo.player.framework.di.dagger

import android.content.Context

fun Context.toPlayerFrameworkComponent(): PlayerFrameworkComponent =
    (applicationContext as PlayerFrameworkComponentProvider).playerFrameworkComponent()

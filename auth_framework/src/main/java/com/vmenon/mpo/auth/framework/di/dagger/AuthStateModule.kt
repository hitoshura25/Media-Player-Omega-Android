package com.vmenon.mpo.auth.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.framework.SharedPrefsAuthState
import dagger.Module
import dagger.Provides

@Module
object AuthStateModule {
    @Provides
    @AuthScope
    fun provideAuthState(application: Application): AuthState = SharedPrefsAuthState(application)
}
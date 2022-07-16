package com.vmenon.mpo.auth.test.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.framework.di.dagger.AuthScope
import com.vmenon.mpo.auth.test.TestSharedPrefsAuthState
import dagger.Module
import dagger.Provides

@Module
object TestAuthStateModule {
    @Provides
    @AuthScope
    fun provideAuthState(application: Application): AuthState =
        TestSharedPrefsAuthState(application)
}
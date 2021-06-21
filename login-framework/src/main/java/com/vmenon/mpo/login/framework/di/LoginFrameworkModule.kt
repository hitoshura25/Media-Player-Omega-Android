package com.vmenon.mpo.login.framework.di

import android.app.Application
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.login.data.LoginRepository
import com.vmenon.mpo.login.data.UserCache
import com.vmenon.mpo.login.data.UserRegistry
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login.framework.MpoApiUserRegistry
import com.vmenon.mpo.login.framework.SharedPrefsUserCache
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit

@Module
object LoginFrameworkModule {
    @Provides
    @LoginFrameworkScope
    fun provideUserCache(
        application: Application,
        system: System
    ): UserCache =
        SharedPrefsUserCache(
            application,
            system,
            TimeUnit.MINUTES.toMillis(15)
        )

    @Provides
    @LoginFrameworkScope
    fun provideLoginService(
        userRegistry: UserRegistry,
        userCache: UserCache,
        system: System
    ): LoginService = LoginRepository(
        userRegistry,
        userCache,
        system
    )

    @Provides
    @LoginFrameworkScope
    fun provideUserRegistry(
        api: MediaPlayerOmegaRetrofitService
    ): UserRegistry = MpoApiUserRegistry(api)
}
package com.vmenon.mpo.login.framework.di

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
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
        commonFrameworkComponent: CommonFrameworkComponent
    ): UserCache =
        SharedPrefsUserCache(
            commonFrameworkComponent.systemFrameworkComponent().application(),
            commonFrameworkComponent.systemFrameworkComponent().system(),
            TimeUnit.MINUTES.toMillis(15)
        )

    @Provides
    @LoginFrameworkScope
    fun provideLoginService(
        userRegistry: UserRegistry,
        userCache: UserCache,
        commonFrameworkComponent: CommonFrameworkComponent
    ): LoginService = LoginRepository(
        userRegistry,
        userCache,
        commonFrameworkComponent.systemFrameworkComponent().system()
    )

    @Provides
    @LoginFrameworkScope
    fun provideUserRegistry(
        api: MediaPlayerOmegaRetrofitService
    ): UserRegistry = MpoApiUserRegistry(api)
}
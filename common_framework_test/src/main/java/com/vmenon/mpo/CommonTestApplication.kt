package com.vmenon.mpo

import android.app.Application
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.framework.di.dagger.TestDaggerComponentProviders
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider

class CommonTestApplication : Application(), CommonFrameworkComponentProvider,
    SystemFrameworkComponentProvider,
    AuthComponentProvider {

    private val componentProviders = TestDaggerComponentProviders(this)

    override fun commonFrameworkComponent(): CommonFrameworkComponent =
        componentProviders.commonFrameworkComponent

    override fun systemFrameworkComponent(): SystemFrameworkComponent =
        componentProviders.systemFrameworkComponent

    override fun authComponent(): AuthComponent = componentProviders.authComponent
}
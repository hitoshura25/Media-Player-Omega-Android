package com.vmenon.mpo.common.framework.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider

class DaggerComponentProviders(
    val systemFrameworkComponent: SystemFrameworkComponent,
    val navigationFrameworkComponent: NavigationFrameworkComponent,
    val persistenceComponent: PersistenceComponent,
    apiUrl: String,
) : SystemFrameworkComponentProvider, AuthComponentProvider, CommonFrameworkComponentProvider {

    private val authComponentProvider = DaggerAuthComponentProvider(systemFrameworkComponent)
    private val commonFrameworkComponentProvider = DaggerCommonFrameworkComponentProvider(
        systemFrameworkComponent,
        authComponentProvider.authComponent(),
        persistenceComponent,
        navigationFrameworkComponent,
        apiUrl
    )

    override fun authComponent(): AuthComponent = authComponentProvider.authComponent()

    override fun commonFrameworkComponent(): CommonFrameworkComponent =
        commonFrameworkComponentProvider.commonFrameworkComponent()

    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
}
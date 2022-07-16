package com.vmenon.mpo.auth.test.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.*
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(
    modules = [AuthModule::class, TestAuthStateModule::class],
    dependencies = [SystemFrameworkComponent::class, TestBiometricsComponent::class]
)
@AuthScope
interface TestAuthComponent : AuthComponent
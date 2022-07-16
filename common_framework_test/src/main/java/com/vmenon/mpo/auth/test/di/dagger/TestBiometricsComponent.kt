package com.vmenon.mpo.auth.test.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsScope
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(modules = [TestBiometricsModule::class], dependencies = [SystemFrameworkComponent::class])
@BiometricsScope
interface TestBiometricsComponent : BiometricsComponent
package com.vmenon.mpo.navigation.framework

import com.vmenon.mpo.navigation.domain.NavigationParams
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


object NavigationParamsSerializer {
    val module = SerializersModule {
        polymorphic(NavigationParams::class) {
            subclass(NoNavigationParams::class)
        }
    }
    val format = Json { serializersModule = module }
}
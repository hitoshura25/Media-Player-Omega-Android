package com.vmenon.mpo

import com.vmenon.mpo.auth.test.MockAndroidBiometricsManager
import com.vmenon.mpo.di.DaggerComponentProviders

class CucumberTestMPOApplication : MPOApplication() {
    lateinit var mockBiometricsManager: MockAndroidBiometricsManager

    override fun onCreate() {
        super.onCreate()
        println("I'm the test app!")
    }

    override fun createComponentProviders(): DaggerComponentProviders {
        val providers = CucumberDaggerComponentProviders(this, "https://localhost:8080/")
        mockBiometricsManager =
            providers.biometricsComponent.biometricsManager() as MockAndroidBiometricsManager
        return providers
    }
}
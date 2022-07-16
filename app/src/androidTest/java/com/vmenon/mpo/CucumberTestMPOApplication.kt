package com.vmenon.mpo

import com.vmenon.mpo.auth.test.MockAndroidBiometricsManager
import com.vmenon.mpo.auth.test.TestSharedPrefsAuthState
import com.vmenon.mpo.di.DaggerComponentProviders
import com.vmenon.mpo.player.domain.PlaybackStateTracker
import com.vmenon.mpo.test.TestPlaybackStateTracker

class CucumberTestMPOApplication : MPOApplication() {
    lateinit var mockBiometricsManager: MockAndroidBiometricsManager
    lateinit var testSharedPrefsAuthState: TestSharedPrefsAuthState
    lateinit var testPlaybackStateTracker: TestPlaybackStateTracker

    override fun onCreate() {
        super.onCreate()
        println("I'm the test app!")
    }

    override fun createComponentProviders(): DaggerComponentProviders {
        val providers = CucumberDaggerComponentProviders(this, "https://localhost:8080/")
        mockBiometricsManager =
            providers.authComponent.biometricsManager() as MockAndroidBiometricsManager
        testSharedPrefsAuthState = providers.authComponent.authState() as TestSharedPrefsAuthState
        return providers
    }

    override fun createPlaybackStateTracker(): PlaybackStateTracker {
        testPlaybackStateTracker = TestPlaybackStateTracker()
        return testPlaybackStateTracker
    }
}
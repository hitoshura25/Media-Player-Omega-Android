package com.vmenon.mpo.auth.framework

import androidx.fragment.app.testing.launchFragmentInContainer
import com.vmenon.mpo.auth.framework.openid.fragment.OpenIdHandlerFragment
import org.junit.Test

class OpenIdHandlerFragmentInstrumentedTest {
    @Test
    fun launch() {
        launchFragmentInContainer<OpenIdHandlerFragment>()
    }
}
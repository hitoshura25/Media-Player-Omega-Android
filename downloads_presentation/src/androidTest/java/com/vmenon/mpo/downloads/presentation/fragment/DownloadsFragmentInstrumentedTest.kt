package com.vmenon.mpo.downloads.presentation.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vmenon.mpo.downloads.R
import org.hamcrest.core.AllOf.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadsFragmentInstrumentedTest {

    @Test
    fun launch() {
        launchFragmentInContainer<DownloadsFragment>()
        onView(
            allOf(
                withId(R.id.toolbar),
                withText("Downloads")
            )
        ).check(matches(isDisplayed()))
    }
}
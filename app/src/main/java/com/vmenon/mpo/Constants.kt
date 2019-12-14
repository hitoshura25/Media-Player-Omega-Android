package com.vmenon.mpo

object Constants {
    const val API_URL = "https://mpospboot.herokuapp.com/"
    const val REPLAY_DURATION = -10
    const val SKIP_DURATION = 30

    // Don't keep skipping past after this to prevent accidentally completing media
    const val MEDIA_SKIP_GRACE_PERIOD = 5
}

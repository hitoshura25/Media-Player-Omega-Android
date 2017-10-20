package com.vmenon.mpo;

public final class Constants {
    public final static String API_URL = "https://mpospboot.herokuapp.com/";
    public final static int REPLAY_DURATION = -10;
    public final static int SKIP_DURATION = 30;

    // Don't keep skipping past after this to prevent accidentally completing media
    public final static int MEDIA_SKIP_GRACE_PERIOD = 5;
}

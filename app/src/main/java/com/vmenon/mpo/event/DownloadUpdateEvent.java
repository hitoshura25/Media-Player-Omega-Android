package com.vmenon.mpo.event;

import com.vmenon.mpo.core.Download;

public class DownloadUpdateEvent {
    public final Download download;

    public DownloadUpdateEvent(final Download download) {
        this.download = download;
    }
}

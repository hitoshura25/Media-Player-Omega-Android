package com.vmenon.mpo.event;

import com.vmenon.mpo.core.Download;

public class DownloadCompleteEvent {
    private final Download download;

    public DownloadCompleteEvent(final Download download) {
        this.download = download;
    }

    public Download getDownload() {
        return download;
    }
}

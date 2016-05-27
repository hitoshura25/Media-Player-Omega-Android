package com.vmenon.mpo.core;

import org.parceler.Parcel;

@Parcel
public class Download {
    private String url;
    private String showName;
    private String episodeName;
    private int total = 0;
    private int progress = 0;

    public Download() {

    }

    public Download(final String showName, final String episodeName, final String url) {
        this.url = url;
        this.showName = showName;
        this.episodeName = episodeName;
    }

    public String getUrl() {
        return url;
    }

    public String getShowName() {
        return showName;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public synchronized int getTotal() {
        return total;
    }

    public synchronized void setTotal(int total) {
        this.total = total;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void addProgress(int progress) {
        this.progress += progress;
    }
}

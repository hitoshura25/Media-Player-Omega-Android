package com.vmenon.mpo.core;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;

import org.parceler.Parcel;

@Parcel
public class Download {
    Show show;
    Episode episode;
    int total = 0;
    int progress = 0;

    public Download() {

    }

    public Download(Show show, Episode episode) {
        this.show = show;
        this.episode = episode;
    }

    public Show getShow() {
        return show;
    }

    public Episode getEpisode() {
        return episode;
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

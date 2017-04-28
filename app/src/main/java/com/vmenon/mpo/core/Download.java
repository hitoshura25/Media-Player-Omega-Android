package com.vmenon.mpo.core;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;

import org.parceler.Parcel;

@Parcel
public class Download {
    Podcast podcast;
    Episode episode;
    int total = 0;
    int progress = 0;

    public Download() {

    }

    public Download(Podcast podcast, Episode episode) {
        this.podcast = podcast;
        this.episode = episode;
    }

    public Podcast getPodcast() {
        return podcast;
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

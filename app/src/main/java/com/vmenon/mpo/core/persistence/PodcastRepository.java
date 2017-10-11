package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PodcastRepository {
    private final MediaPlayerOmegaService service;
    private final PodcastDao podcastDao;
    private final EpisodeDao episodeDao;
    private final Executor discExecutor;

    public PodcastRepository(MediaPlayerOmegaService service, PodcastDao podcastDao, EpisodeDao episodeDao) {
        this.service = service;
        this.podcastDao = podcastDao;
        this.episodeDao = episodeDao;
        this.discExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Podcast>> getAllPodcasts() {
        return podcastDao.load();
    }

    public void save(final Podcast podcast) {
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                podcastDao.save(podcast);
            }
        });
    }

    public List<Podcast> notUpdatedInLast(long interval) {
        final long compareTime = new Date().getTime() - interval;
        return podcastDao.loadLastUpdatedBefore(compareTime);
    }

    public void save(final Episode episode) {
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                episodeDao.save(episode);
            }
        });
    }
}

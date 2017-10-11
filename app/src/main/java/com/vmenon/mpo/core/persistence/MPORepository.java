package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MPORepository {
    private final MediaPlayerOmegaService service;
    private final ShowDao showDao;
    private final EpisodeDao episodeDao;
    private final Executor discExecutor;

    public MPORepository(MediaPlayerOmegaService service, ShowDao showDao, EpisodeDao episodeDao) {
        this.service = service;
        this.showDao = showDao;
        this.episodeDao = episodeDao;
        this.discExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Show>> getAllShows() {
        return showDao.load();
    }

    public void save(final Show show) {
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                showDao.save(show);
            }
        });
    }

    public List<Show> notUpdatedInLast(long interval) {
        final long compareTime = new Date().getTime() - interval;
        return showDao.loadLastUpdatedBefore(compareTime);
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

package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MPORepository {

    // Listener used to signal data being fetched. Workaround for cases where LiveData cannot be used
    // i.e., in MediaBrowserService instances as they don't extend LifeCycleService
    public interface DataHandler<T> {
        void onDataReady(T data);
    }

    private final MediaPlayerOmegaService service;
    private final ShowDao showDao;
    private final EpisodeDao episodeDao;
    private final Executor discExecutor;
    private final Executor mainThreadExecutor;

    public MPORepository(MediaPlayerOmegaService service, ShowDao showDao, EpisodeDao episodeDao) {
        this.service = service;
        this.showDao = showDao;
        this.episodeDao = episodeDao;
        this.discExecutor = Executors.newSingleThreadExecutor();
        this.mainThreadExecutor = new MainThreadExecutor();
    }

    public LiveData<List<Show>> getAllShows() {
        return showDao.load();
    }

    public void fetchShow(final long id, final DataHandler<Show> dataHandler) {
        // TODO: Caching
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Show show = showDao.getById(id);
                mainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        dataHandler.onDataReady(show);
                    }
                });
            }
        });
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

    public LiveData<Episode> getLiveEpisode(long id) {
        // TODO: Cache
        return episodeDao.liveById(id);
    }

    public void fetchEpisode(final long id, final DataHandler<Episode> dataHandler) {
        // TODO: Implement cache
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Episode episode = episodeDao.byId(id);
                mainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        dataHandler.onDataReady(episode);
                    }
                });
            }
        });
    }

    public void save(final Episode episode) {
        discExecutor.execute(new Runnable() {
            @Override
            public void run() {
                episodeDao.save(episode);
            }
        });
    }

    public LiveData<List<Episode>> getAllEpisodes() {
        return episodeDao.load();
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

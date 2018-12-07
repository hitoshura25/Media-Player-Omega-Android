package com.vmenon.mpo.core;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.event.DownloadUpdateEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private static int NUMBER_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static final int STATE_UPDATE = 1;

    private final Handler handler;
    private final BlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Context context;
    private final Map<String, Download> currentDownloads = new ConcurrentHashMap<>();
    private MutableLiveData<List<Download>> downloadLiveData = new MutableLiveData<>();

    private final MPORepository mpoRepository;

    public DownloadManager(final Context context, final MPORepository mpoRepository) {
        this.context = context;
        this.mpoRepository = mpoRepository;

        workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(NUMBER_CORES, NUMBER_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, workQueue);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATE_UPDATE:
                        DownloadUpdateEvent event = (DownloadUpdateEvent) msg.obj;
                        Log.d("MPO", "got update: " + event.getDownload().getProgress());
                        updateLiveData();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public LiveData<List<Download>> getDownloads() {
        return downloadLiveData;
    }

    public void queueDownload(final Download download) {
        final Show show = download.getShow();
        final Episode episode = download.getEpisode();
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                currentDownloads.put(episode.downloadUrl, download);

                InputStream input = null;
                OutputStream output = null;
                String filename = URLUtil.guessFileName(episode.downloadUrl, null, null);
                File showDir = new File(context.getFilesDir(), show.name);
                showDir.mkdir();
                File episodeFile = new File(showDir, filename);
                episode.filename = episodeFile.getPath();

                try {
                    URL url = new URL(episode.downloadUrl);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    download.setTotal(connection.getContentLength());
                    input = new BufferedInputStream(connection.getInputStream());
                    output = new FileOutputStream(episodeFile);

                    byte data[] = new byte[1024];
                    int count;
                    long lastPost = 0;

                    while ((count = input.read(data)) != -1) {
                        download.addProgress(count);
                        output.write(data, 0, count);
                        DownloadUpdateEvent downloadUpdateEvent = new DownloadUpdateEvent(download);
                        Log.d("MPO", "Progress: " + download.getProgress() + "/" + download.getTotal());

                        // only post message every so often to avoid too many ui updates
                        if (System.currentTimeMillis() - lastPost > 3000) {
                            Message completeMessage =
                                    handler.obtainMessage(STATE_UPDATE, downloadUpdateEvent);
                            completeMessage.sendToTarget();
                            lastPost = System.currentTimeMillis();
                        }
                    }

                    output.flush();
                    show.lastEpisodePublished = episode.published;
                    episode.showId = show.id;
                    mpoRepository.save(episode);
                    mpoRepository.save(show);
                } catch (Exception e) {
                    Log.e("MPO", "Error downloading file: " + episode.downloadUrl, e);
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            Log.w("MPO", "Error closing output", e);
                        }
                    }

                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            Log.w("MPO", "Error closing input", e);
                        }
                    }
                }

                currentDownloads.remove(episode.downloadUrl);
                updateLiveData();
            }
        });
        Log.d("MPO", "Queued download: " + download.getEpisode().downloadUrl);
    }

    private void updateLiveData() {
        downloadLiveData.postValue(new ArrayList<>(currentDownloads.values()));
    }
}

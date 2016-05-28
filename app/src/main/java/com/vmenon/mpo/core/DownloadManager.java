package com.vmenon.mpo.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.vmenon.mpo.event.DownloadUpdateEvent;

import java.io.BufferedInputStream;
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

    protected final EventBus eventBus;

    public DownloadManager(final Context context, final EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;

        workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(NUMBER_CORES, NUMBER_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, workQueue);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATE_UPDATE:
                        DownloadUpdateEvent event = (DownloadUpdateEvent) msg.obj;
                        Log.d("MPO", "got update: " + event.download.getProgress());
                        eventBus.send(event);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public List<Download> getDownloads() {
        return new ArrayList<>(currentDownloads.values());
    }

    public void queueDownload(final Download download) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                currentDownloads.put(download.getUrl(), download);

                InputStream input = null;
                OutputStream output = null;

                try {
                    URL url = new URL(download.getUrl());
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    download.setTotal(connection.getContentLength());
                    input = new BufferedInputStream(connection.getInputStream());
                    output = context.openFileOutput(download.getEpisodeName(),
                            Context.MODE_PRIVATE);

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
                } catch (Exception e) {
                    Log.e("MPO", "Error downloading file: " + download.getUrl(), e);
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

                currentDownloads.remove(download.getUrl());
            }
        });
    }
}

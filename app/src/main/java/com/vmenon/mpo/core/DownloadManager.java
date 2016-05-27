package com.vmenon.mpo.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
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

    private final Handler handler;
    private final BlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Context context;
    private final Map<String, Download> currentDownloads = new ConcurrentHashMap<>();

    public DownloadManager(Context context) {
        this.context = context;
        workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(NUMBER_CORES, NUMBER_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, workQueue);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    public Collection<Download> getDownloads() {
        return currentDownloads.values();
    }

    public void queueDownload(final Download download) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
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
                    while ((count = input.read(data)) != -1) {
                        download.addProgress(count);
                        output.write(data, 0, count);
                        Log.d("MPO", "Progress: " + download.getProgress() + "/" + download.getTotal());
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

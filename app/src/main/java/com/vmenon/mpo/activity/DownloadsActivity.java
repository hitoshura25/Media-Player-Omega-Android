package com.vmenon.mpo.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.DownloadsAdapter;
import com.vmenon.mpo.core.Download;
import com.vmenon.mpo.core.DownloadManager;
import com.vmenon.mpo.core.EventBus;
import com.vmenon.mpo.event.DownloadUpdateEvent;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class DownloadsActivity extends BaseActivity {

    @Inject
    protected DownloadManager downloadManager;

    @Inject
    protected EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        setContentView(R.layout.activity_downloads);

        final List<Download> downloads = downloadManager.getDownloads();
        final DownloadsAdapter adapter = new DownloadsAdapter(downloads);
        RecyclerView downloadList = (RecyclerView) findViewById(R.id.downloadsList);
        downloadList.setLayoutManager(new LinearLayoutManager(this));
        downloadList.setAdapter(adapter);

        eventBus.subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof DownloadUpdateEvent) {
                    DownloadUpdateEvent downloadEvent = (DownloadUpdateEvent) event;
                    adapter.notifyItemChanged(downloads.indexOf(downloadEvent.getDownload()));
                }
            }
        });
    }
}

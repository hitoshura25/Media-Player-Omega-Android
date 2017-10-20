package com.vmenon.mpo.activity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.DownloadsAdapter;
import com.vmenon.mpo.core.Download;
import com.vmenon.mpo.core.DownloadManager;

import java.util.List;

import javax.inject.Inject;

public class DownloadsActivity extends BaseDrawerActivity {

    @Inject
    protected DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        final RecyclerView downloadList = findViewById(R.id.downloadsList);
        downloadList.setLayoutManager(new LinearLayoutManager(this));
        downloadManager.getDownloads().observe(this, new Observer<List<Download>>() {
            @Override
            public void onChanged(@Nullable List<Download> downloads) {
                final DownloadsAdapter adapter = new DownloadsAdapter(downloads);
                downloadList.setAdapter(adapter);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_downloads;
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_downloads;
    }

    @Override
    protected boolean isRootActivity() {
        return true;
    }
}

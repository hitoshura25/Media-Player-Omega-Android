package com.vmenon.mpo.activity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.LibraryAdapter;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.core.persistence.MPORepository;

import java.util.List;

import javax.inject.Inject;

public class LibraryActivity extends BaseActivity {

    @Inject
    protected MPORepository mpoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        setContentView(R.layout.activity_library);
        final RecyclerView libraryList = findViewById(R.id.libraryList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        libraryList.setHasFixedSize(true);
        libraryList.setLayoutManager(layoutManager);
        libraryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mpoRepository.getAllEpisodes().observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {
                LibraryAdapter adapter = new LibraryAdapter(episodes);
                libraryList.setAdapter(adapter);
            }
        });
    }
}
package com.vmenon.mpo.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.EpisodesAdapter;
import com.vmenon.mpo.adapter.LibraryAdapter;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.core.persistence.MPORepository;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class LibraryActivity extends BaseDrawerActivity implements EpisodesAdapter.EpisodeSelectedListener {

    @Inject
    protected MPORepository mpoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        final RecyclerView libraryList = findViewById(R.id.libraryList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        libraryList.setHasFixedSize(true);
        libraryList.setLayoutManager(layoutManager);
        libraryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mpoRepository.getAllEpisodes().observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {
                LibraryAdapter adapter = new LibraryAdapter(episodes);
                adapter.setListener(LibraryActivity.this);
                libraryList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onEpisodeSelected(Episode episode) {
        Intent intent = new Intent(this, EpisodeDetailsActivity.class);
        intent.putExtra(EpisodeDetailsActivity.EXTRA_EPISODE, Parcels.wrap(episode));
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_library;
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_library;
    }

    @Override
    protected boolean isRootActivity() {
        return true;
    }
}
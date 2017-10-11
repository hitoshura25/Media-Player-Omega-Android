package com.vmenon.mpo.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.PodcastSearchResultsAdapter;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;
import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PodcastSearchResultsActivity extends BaseActivity implements
        PodcastSearchResultsAdapter.PodcastSelectedListener {

    @Inject
    protected MediaPlayerOmegaService service;

    private RecyclerView podcastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        setContentView(R.layout.activity_podcast_search_results);

        podcastList = findViewById(R.id.podcastList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        podcastList.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        podcastList.setLayoutManager(layoutManager);
        podcastList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        handleIntent(getIntent());
    }

    @Override
    public void onPodcastSelected(Podcast podcast) {
        Intent intent = new Intent(this, PodcastDetailsActivity.class);
        intent.putExtra(PodcastDetailsActivity.EXTRA_PODCAST, Parcels.wrap(podcast));
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setTitle(this.getString(R.string.podcast_search_title, query));

            service.searchPodcasts(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<List<Podcast>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull List<Podcast> podcasts) {
                            PodcastSearchResultsAdapter adapter = new PodcastSearchResultsAdapter(podcasts);
                            adapter.setListener(PodcastSearchResultsActivity.this);
                            podcastList.setAdapter(adapter);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.w("MPO", "Error search for podcasts", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }


}
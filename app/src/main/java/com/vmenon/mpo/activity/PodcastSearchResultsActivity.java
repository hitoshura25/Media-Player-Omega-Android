package com.vmenon.mpo.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.PodcastSearchResultsAdapter;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.service.MediaPlayerOmegaService;
import com.vmenon.mpo.service.ServiceFactory;

import org.parceler.Parcels;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PodcastSearchResultsActivity extends AppCompatActivity implements
        PodcastSearchResultsAdapter.PodcastSelectedListener {
    private MediaPlayerOmegaService service;
    private RecyclerView podcastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            service = ServiceFactory.newInstance();
        }

        setContentView(R.layout.activity_podcast_search_results);

        podcastList = (RecyclerView) findViewById(R.id.podcastList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        podcastList.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        podcastList.setLayoutManager(layoutManager);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
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
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Podcast>>() {
                        @Override
                        public final void onCompleted() {
                            // do nothing
                        }

                        @Override
                        public final void onError(Throwable e) {
                            Log.e("Error getting podcasts", e.getMessage());
                        }

                        @Override
                        public final void onNext(List<Podcast> response) {
                            PodcastSearchResultsAdapter adapter = new PodcastSearchResultsAdapter(response);
                            adapter.setListener(PodcastSearchResultsActivity.this);
                            podcastList.setAdapter(adapter);
                        }
                    });
        }
    }


}
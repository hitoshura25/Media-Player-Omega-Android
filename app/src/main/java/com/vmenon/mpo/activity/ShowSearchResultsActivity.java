package com.vmenon.mpo.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.ShowSearchResultsAdapter;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;
import java.util.List;
import javax.inject.Inject;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShowSearchResultsActivity extends BaseActivity implements
        ShowSearchResultsAdapter.ShowSelectedListener {

    @Inject
    protected MediaPlayerOmegaService service;

    private RecyclerView showList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        setContentView(R.layout.activity_show_search_results);

        showList = findViewById(R.id.showList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        showList.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        showList.setLayoutManager(layoutManager);
        showList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        handleIntent(getIntent());
    }

    @Override
    public void onShowSelected(Show show) {
        Intent intent = new Intent(this, ShowDetailsActivity.class);
        intent.putExtra(ShowDetailsActivity.EXTRA_SHOW, Parcels.wrap(show));
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setTitle(this.getString(R.string.show_search_title, query));

            service.searchPodcasts(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<List<Show>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull List<Show> shows) {
                            ShowSearchResultsAdapter adapter = new ShowSearchResultsAdapter(shows);
                            adapter.setListener(ShowSearchResultsActivity.this);
                            showList.setAdapter(adapter);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.w("MPO", "Error search for shows", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }


}
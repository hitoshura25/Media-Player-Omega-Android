package com.vmenon.mpo.activity;

import android.app.SearchManager;
import androidx.lifecycle.Observer;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.SubscriptionGalleryAdapter;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.BackgroundService;
import com.vmenon.mpo.core.persistence.MPORepository;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseDrawerActivity {

    @Inject
    protected MPORepository mpoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setTitle(R.string.shows);
        BackgroundService.setupSchedule(this);
        final RecyclerView recyclerView = findViewById(R.id.showList);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mpoRepository.getAllShows().observe(this, new Observer<List<Show>>() {
            @Override
            public void onChanged(@Nullable List<Show> shows) {
                Log.d("MPO", "Got " + shows.size() + " shows");
                SubscriptionGalleryAdapter adapter = new SubscriptionGalleryAdapter(shows);
                adapter.setHasStableIds(true);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_home;
    }

    @Override
    protected boolean isRootActivity() {
        return true;
    }

}

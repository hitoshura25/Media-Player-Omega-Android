package com.vmenon.mpo.activity;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.SubscriptionGalleryAdapter;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.core.BackgroundService;
import com.vmenon.mpo.core.persistence.PodcastRepository;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    protected PodcastRepository podcastRepository;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        setTitle(R.string.podcasts);
        BackgroundService.setupSchedule(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if (R.id.nav_downloads == menuItem.getItemId()) {
                                Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
                                startActivity(intent);
                            }

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        final RecyclerView recyclerView = findViewById(R.id.podcastList);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        podcastRepository.getAllPodcasts().observe(this, new Observer<List<Podcast>>() {
            @Override
            public void onChanged(@Nullable List<Podcast> podcasts) {
                Log.d("MPO", "Got " + podcasts.size() + " podcasts");
                SubscriptionGalleryAdapter adapter = new SubscriptionGalleryAdapter(podcasts);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

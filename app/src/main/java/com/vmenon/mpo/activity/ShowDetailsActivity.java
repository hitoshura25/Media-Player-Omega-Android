package com.vmenon.mpo.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.EpisodesAdapter;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.api.ShowDetails;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ShowDetailsActivity extends BaseActivity implements
        AppBarLayout.OnOffsetChangedListener {
    public final static String EXTRA_SHOW = "extraShow";

    @Inject
    protected MediaPlayerOmegaService service;

    @Inject
    protected MPORepository mpoRepository;

    private CollapsingToolbarLayout collapsingToolbar;
    private TextView descriptionText;
    private ImageView showImage;
    private ViewGroup detailsContainer;
    private Show show;

    private boolean collapsed = false;
    private int scrollRange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);

        setContentView(R.layout.activity_show_details);

        descriptionText = findViewById(R.id.showDescription);
        showImage = findViewById(R.id.showImage);
        detailsContainer = findViewById(R.id.detailsContainer);
        show = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_SHOW));

        final AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if (R.id.nav_downloads == menuItem.getItemId()) {
                                Intent intent = new Intent(ShowDetailsActivity.this,
                                        DownloadsActivity.class);
                                startActivity(intent);
                            }

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }

        if (savedInstanceState == null) {
            service.getPodcastDetails(show.feedUrl, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ShowDetails>() {
                        @Override
                        public void accept(ShowDetails showDetails) throws Exception {
                            displayDetails(showDetails);
                        }
                    });
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbar.setTitle(show.name);
            collapsed = true;
        } else if (collapsed) {
            collapsingToolbar.setTitle("");
            collapsed = false;
        }
    }

    private void displayDetails(ShowDetails showDetails) {
        descriptionText.setText(Html.fromHtml(showDetails.getDescription()));
        Glide.with(this).load(showDetails.getImageUrl()).fitCenter().into(showImage);
        RecyclerView episodeList = findViewById(R.id.episodesList);

        episodeList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        episodeList.setLayoutManager(layoutManager);
        episodeList.setAdapter(new EpisodesAdapter(show, showDetails.getEpisodes()));

        final View nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        nestedScrollView.setScrollY(0);
                        nestedScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

        final View.OnClickListener undoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MPO", "User clicked undo");
            }
        };

        final View subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpoRepository.save(show);
                Snackbar.make(detailsContainer, "You have subscribed to this show",
                        Snackbar.LENGTH_LONG)
                        .setAction("UNDO", undoListener)
                        .show();
            }
        });
    }
}
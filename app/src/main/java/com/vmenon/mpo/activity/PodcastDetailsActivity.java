package com.vmenon.mpo.activity;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.adapter.PodcastEpisodesAdapter;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.api.PodcastDetails;
import com.vmenon.mpo.service.MediaPlayerOmegaService;
import com.vmenon.mpo.service.ServiceFactory;

import org.parceler.Parcels;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PodcastDetailsActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener {
    public final static String EXTRA_PODCAST = "extraPodcast";

    private CollapsingToolbarLayout collapsingToolbar;
    private TextView descriptionText;
    private ImageView podcastImage;
    private ViewGroup detailsContainer;
    private Podcast podcast;

    private boolean collapsed = false;
    private int scrollRange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_details);

        descriptionText = (TextView) findViewById(R.id.podcastDescription);
        podcastImage = (ImageView) findViewById(R.id.podcastImage);
        detailsContainer = (ViewGroup) findViewById(R.id.detailsContainer);
        podcast = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_PODCAST));

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        if (savedInstanceState == null) {
            MediaPlayerOmegaService service = ServiceFactory.newInstance();
            service.getPodcastDetails(podcast.feedUrl)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<PodcastDetails>() {
                        @Override
                        public final void onCompleted() {

                        }

                        @Override
                        public final void onError(Throwable e) {
                            Log.e("MPO", "Error getting podcast details", e);
                        }

                        @Override
                        public final void onNext(PodcastDetails podcastDetails) {
                            displayPodcastDetails(podcastDetails);
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
            collapsingToolbar.setTitle(podcast.name);
            collapsed = true;
        } else if (collapsed) {
            collapsingToolbar.setTitle("");
            collapsed = false;
        }
    }

    private void displayPodcastDetails(PodcastDetails podcastDetails) {
        descriptionText.setText(Html.fromHtml(podcastDetails.getDescription()));
        Glide.with(this).load(podcastDetails.getImageUrl()).fitCenter().into(podcastImage);

        for (int i = 0; i < podcastDetails.getEpisodes().size() && i < 10; i++) {
            Episode episode = podcastDetails.getEpisodes().get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.recent_episode, detailsContainer,
                    false);
            TextView episodeNameText = (TextView) view.findViewById(R.id.episodeName);
            WebView episodeDescriptionText = (WebView) view.findViewById(R.id.episodeDescription);
            episodeNameText.setText(episode.getName());
            episodeDescriptionText.loadData(episode.getDescription(), "text/html", "UTF-8");
            episodeDescriptionText.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.setBackgroundColor(0x00000000);
                    if (Build.VERSION.SDK_INT >= 11) {
                        view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                    }
                }
            });
            detailsContainer.addView(view);
        }
    }
}
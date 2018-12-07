package com.vmenon.mpo.activity;

import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.persistence.MPORepository;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class EpisodeDetailsActivity extends BaseDrawerCollapsingToolbarActivity {

    public final static String EXTRA_EPISODE = "extraEpisode";

    @Inject
    protected MPORepository repository;

    private Episode episode;
    private Show show;

    private ImageView appBarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        episode = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_EPISODE));

        TextView nameText = findViewById(R.id.episodeName);
        TextView descriptionText = findViewById(R.id.episodeDescription);
        TextView publishedText = findViewById(R.id.episodeDate);
        final ImageView imageView = findViewById(R.id.episodeImage);
        nameText.setText(episode.name);
        descriptionText.setText(Html.fromHtml(
                episode.description.replaceAll("(<(//)img>)|(<img.+?>)", "")));
        publishedText.setText(new SimpleDateFormat().format(
                new Date(episode.published)));
        repository.getLiveShow(episode.showId).observe(this, new Observer<Show>() {
            @Override
            public void onChanged(@Nullable Show show) {
                EpisodeDetailsActivity.this.show = show;
                Glide.with(EpisodeDetailsActivity.this).
                        load(show.artworkUrl)
                        .into(appBarImage);

                if (episode.artworkUrl != null && !episode.artworkUrl.equals(show.artworkUrl)) {
                    Glide.with(EpisodeDetailsActivity.this).load(episode.artworkUrl).fitCenter().into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        });

        appBarImage = findViewById(R.id.appBarImage);
    }

    @Override
    protected int getFabDrawableResource() {
        return R.drawable.ic_play_arrow_white_48dp;
    }

    @Override
    protected CharSequence getCollapsedToolbarTitle() {
        return show != null ? show.name : "";
    }

    @Override
    protected CharSequence getExpandedToolbarTitle() {
        return "";
    }

    @Override
    protected void onFabClick() {
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        intent.putExtra(MediaPlayerActivity.EXTRA_EPISODE, Parcels.wrap(episode));
        startActivity(intent);
    }

    @Override
    protected int getCollapsiblePanelContentLayoutId() {
        return R.layout.episode_details_panel_content;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_episode_details;
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_library;
    }
}

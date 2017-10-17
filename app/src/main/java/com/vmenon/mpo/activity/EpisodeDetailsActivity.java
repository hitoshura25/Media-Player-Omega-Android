package com.vmenon.mpo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EpisodeDetailsActivity extends BaseDrawerCollapsingToolbarActivity {

    public final static String EXTRA_EPISODE = "extraEpisode";

    private Episode episode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        episode = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_EPISODE));
        Glide.with(this).load(episode.artworkUrl).fitCenter().into(appBarImage);

        TextView nameText = findViewById(R.id.episodeName);
        TextView descriptionText = findViewById(R.id.episodeDescription);
        TextView publishedText = findViewById(R.id.episodeDate);
        nameText.setText(episode.name);
        descriptionText.setText(Html.fromHtml(
                episode.description.replaceAll("(<(//)img>)|(<img.+?>)", "")));
        publishedText.setText(new SimpleDateFormat().format(
                new Date(episode.published)));
    }

    @Override
    protected int getFabDrawableResource() {
        return R.drawable.ic_play_arrow_white_48dp;
    }

    @Override
    protected CharSequence getCollapsedToolbarTitle() {
        return episode != null ? episode.name : "";
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
    protected int getLayoutResourceId() {
        return R.layout.activity_episode_details;
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_library;
    }
}

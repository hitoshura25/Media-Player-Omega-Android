package com.vmenon.mpo.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.BackgroundService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EpisodesAdapter extends
        RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    public interface EpisodeSelectedListener {
        void onEpisodeSelected(Episode episode);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private TextView publishedText;
        private ImageView thumbnailImage;
        private ImageButton menuButton;
        private Episode episode;
        private Show show;

        public ViewHolder(View v) {
            super(v);
            nameText = v.findViewById(R.id.episodeName);
            descriptionText = v.findViewById(R.id.episodeDescription);
            publishedText = v.findViewById(R.id.episodeDate);
            thumbnailImage = v.findViewById(R.id.episodeImage);
            menuButton = v.findViewById(R.id.episodeMenuButton);
            descriptionText.setMovementMethod(new LinkMovementMethod());
        }
    }

    private final Show show;
    private List<Episode> episodes;
    private EpisodeSelectedListener listener;

    public EpisodesAdapter(final Show show, final List<Episode> myDataset) {
        this.show = show;
        this.episodes = myDataset;
    }

    public void setListener(EpisodeSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_episode, parent,
                false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && vh.episode != null) {
                    listener.onEpisodeSelected(vh.episode);
                }
            }
        });

        vh.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(vh.menuButton.getContext(), vh.menuButton);
                popupMenu.inflate(R.menu.episode_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (R.id.download_episode == item.getItemId()) {
                            BackgroundService.startDownload(vh.menuButton.getContext(),
                                    vh.show,
                                    vh.episode);
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.episode = episode;
        holder.show = show;
        holder.nameText.setText(episode.name);
        holder.descriptionText.setText(Html.fromHtml(
                episode.description.replaceAll("(<(//)img>)|(<img.+?>)", "")));
        holder.publishedText.setText(new SimpleDateFormat().format(
                new Date(episode.published)));

        if (episode.artworkUrl != null) {
            Glide.with(holder.thumbnailImage.getContext())
                    .load(episode.artworkUrl)
                    .fitCenter()
                    .into(holder.thumbnailImage);
            holder.thumbnailImage.setVisibility(View.VISIBLE);
        } else {
            holder.thumbnailImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }
}

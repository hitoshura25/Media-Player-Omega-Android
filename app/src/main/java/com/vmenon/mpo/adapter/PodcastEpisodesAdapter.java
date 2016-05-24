package com.vmenon.mpo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;

import java.util.List;

public class PodcastEpisodesAdapter extends
        RecyclerView.Adapter<PodcastEpisodesAdapter.ViewHolder> {

    public interface EpisodeSelectedListener {
        void onEpisodeSelected(Episode episode);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private Episode episode;

        public ViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.episodeName);
            descriptionText = (TextView) v.findViewById(R.id.episodeDescription);
        }
    }

    private List<Episode> episodes;
    private EpisodeSelectedListener listener;

    public PodcastEpisodesAdapter(List<Episode> myDataset) {
        episodes = myDataset;
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

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.episode = episode;
        holder.nameText.setText(episode.getName());
        holder.descriptionText.setText(episode.getDescription());
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }
}

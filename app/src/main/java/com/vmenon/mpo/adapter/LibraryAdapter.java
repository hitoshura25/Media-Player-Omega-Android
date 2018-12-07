package com.vmenon.mpo.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    public interface LibarySelectedListener {
        void onEpisodeSelected(Episode episode);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView imageView;
        private Episode episode;

        public ViewHolder(View v) {
            super(v);
            nameText = v.findViewById(R.id.episodeName);
            imageView = v.findViewById(R.id.episodeImage);
        }
    }

    private List<Episode> episodes;
    private EpisodesAdapter.EpisodeSelectedListener listener;

    public LibraryAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent,
                false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
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
        holder.nameText.setText(episode.name);

        Glide.with(holder.itemView.getContext())
                .load(episode.artworkUrl)
                .centerCrop()
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void setListener(EpisodesAdapter.EpisodeSelectedListener listener) {
        this.listener = listener;
    }
}

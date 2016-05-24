package com.vmenon.mpo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Podcast;

import java.util.List;

public class PodcastSearchResultsAdapter extends
        RecyclerView.Adapter<PodcastSearchResultsAdapter.ViewHolder> {

    public interface PodcastSelectedListener {
        void onPodcastSelected(Podcast podcast);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView imageView;
        private Podcast podcast;

        public ViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.podcastName);
            imageView = (ImageView) v.findViewById(R.id.podcastImage);
        }
    }

    private List<Podcast> podcasts;
    private PodcastSelectedListener listener;

    public PodcastSearchResultsAdapter(List<Podcast> myDataset) {
        podcasts = myDataset;
    }

    public void setListener(PodcastSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_result, parent,
                false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && vh.podcast != null) {
                    listener.onPodcastSelected(vh.podcast);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Podcast podcast = podcasts.get(position);
        holder.podcast = podcast;
        holder.nameText.setText(podcast.name);

        Glide.with(holder.itemView.getContext())
                .load(podcast.artworkUrl)
                .centerCrop()
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return podcasts.size();
    }
}

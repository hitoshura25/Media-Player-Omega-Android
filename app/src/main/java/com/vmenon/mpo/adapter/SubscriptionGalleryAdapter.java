package com.vmenon.mpo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Podcast;

import java.util.List;

public class SubscriptionGalleryAdapter extends
        RecyclerView.Adapter<SubscriptionGalleryAdapter.ViewHolder> {

    public interface PodcastSelectedListener {
        void onPodcastSelected(Podcast podcast);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Podcast podcast;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.podcastImage);
        }
    }

    private List<Podcast> podcasts;
    private PodcastSelectedListener listener;

    public SubscriptionGalleryAdapter(List<Podcast> myDataset) {
        podcasts = myDataset;
    }

    public void setListener(PodcastSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.subscription_gallery_item, parent, false);
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

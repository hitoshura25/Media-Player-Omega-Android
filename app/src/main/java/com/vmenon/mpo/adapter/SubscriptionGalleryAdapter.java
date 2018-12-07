package com.vmenon.mpo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Show;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionGalleryAdapter extends
        RecyclerView.Adapter<SubscriptionGalleryAdapter.ViewHolder> {

    public interface ShowSelectedListener {
        void onShowSelected(Show show);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Show show;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.showImage);
        }
    }

    private List<Show> shows;
    private ShowSelectedListener listener;

    public SubscriptionGalleryAdapter(List<Show> myDataset) {
        shows = myDataset;
    }

    public void setListener(ShowSelectedListener listener) {
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
                if (listener != null && vh.show != null) {
                    listener.onShowSelected(vh.show);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Show show = shows.get(position);
        holder.show = show;

        Glide.with(holder.itemView.getContext())
                .load(show.artworkUrl)
                .fitCenter()
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }
}

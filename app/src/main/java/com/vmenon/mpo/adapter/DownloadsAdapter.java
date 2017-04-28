package com.vmenon.mpo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.core.Download;

import java.util.List;

public class DownloadsAdapter extends
        RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView progressText;
        private ImageView imageView;
        private Download download;

        public ViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.episodeName);
            imageView = (ImageView) v.findViewById(R.id.podcastImage);
            progressText = (TextView) v.findViewById(R.id.progress);
        }
    }

    private List<Download> downloads;

    public DownloadsAdapter(List<Download> myDataset) {
        downloads = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.download, parent,
                false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Download download = downloads.get(position);
        holder.download = download;
        holder.nameText.setText(download.getEpisode().name);

        double progress = download.getProgress() * 1.0 / download.getTotal();
        progress *= 100.0;
        holder.progressText.setText(Math.round(progress) + "%");

        Glide.with(holder.itemView.getContext())
                .load(download.getEpisode().artworkUrl)
                .centerCrop()
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return downloads.size();
    }
}

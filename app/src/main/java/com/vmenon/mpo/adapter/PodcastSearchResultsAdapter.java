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
    private List<Podcast> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTextView;
        private ImageView imageView;
        private View rootView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.podcastName);
            imageView = (ImageView) v.findViewById(R.id.podcastImage);
            rootView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PodcastSearchResultsAdapter(List<Podcast> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.podcast_result, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Podcast podcast = mDataset.get(position);
        holder.mTextView.setText(podcast.name);

        Glide.with(holder.rootView.getContext())
                .load(podcast.artworkUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_search_white_48dp)
                .crossFade()
                .into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

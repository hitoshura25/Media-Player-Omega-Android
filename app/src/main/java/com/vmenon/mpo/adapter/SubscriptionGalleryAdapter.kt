package com.vmenon.mpo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.api.Show

import androidx.recyclerview.widget.RecyclerView

class SubscriptionGalleryAdapter(private val shows: List<Show>) :
    RecyclerView.Adapter<SubscriptionGalleryAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null

    interface ShowSelectedListener {
        fun onShowSelected(show: Show?)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imageView: ImageView
        var show: Show? = null

        init {
            imageView = v.findViewById(R.id.showImage)
        }
    }

    fun setListener(listener: ShowSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.subscription_gallery_item, parent, false
        )
        val vh = ViewHolder(v)
        v.setOnClickListener {
            if (listener != null && vh.show != null) {
                listener!!.onShowSelected(vh.show)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.show = show

        Glide.with(holder.itemView.context)
            .load(show.artworkUrl)
            .fitCenter()
            .crossFade()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

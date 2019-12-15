package com.vmenon.mpo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.SubscribedShowModel

import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.subscription_gallery_item.view.*

class SubscriptionGalleryAdapter(private val shows: List<SubscribedShowModel>) :
    RecyclerView.Adapter<SubscriptionGalleryAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null

    interface ShowSelectedListener {
        fun onShowSelected(show: SubscribedShowModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imageView: ImageView = v.showImage
        var show: SubscribedShowModel? = null
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
            vh.show?.let {
                listener?.onShowSelected(it)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.show = show

        Glide.with(holder.itemView.context)
            .load(show.show.artworkUrl)
            .fitCenter()
            .crossFade()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

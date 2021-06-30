package com.vmenon.mpo.library.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vmenon.mpo.library.databinding.SubscriptionGalleryItemBinding
import com.vmenon.mpo.my_library.domain.ShowModel

class SubscriptionGalleryAdapter(private val shows: List<ShowModel>) :
    RecyclerView.Adapter<SubscriptionGalleryAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null

    interface ShowSelectedListener {
        fun onShowSelected(show: ShowModel)
    }

    class ViewHolder(binding: SubscriptionGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.showImage
        var show: ShowModel? = null
    }

    fun setListener(listener: ShowSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SubscriptionGalleryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val vh = ViewHolder(binding)
        binding.root.setOnClickListener {
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
            .load(show.artworkUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .fitCenter()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

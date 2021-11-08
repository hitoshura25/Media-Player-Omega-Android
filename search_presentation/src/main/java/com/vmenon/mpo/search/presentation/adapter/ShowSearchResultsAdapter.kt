package com.vmenon.mpo.search.presentation.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vmenon.mpo.search.presentation.databinding.ShowResultBinding
import com.vmenon.mpo.search.domain.ShowSearchResultModel

class ShowSearchResultsAdapter :
    RecyclerView.Adapter<ShowSearchResultsAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null
    private val shows = ArrayList<ShowSearchResultModel>()

    interface ShowSelectedListener {
        fun onShowSelected(show: ShowSearchResultModel)
    }

    class ViewHolder(binding: ShowResultBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.showName
        val imageView: ImageView = binding.showImage
    }

    fun setListener(listener: ShowSelectedListener) {
        this.listener = listener
    }

    fun update(searchResults: List<ShowSearchResultModel>, diffResult: DiffUtil.DiffResult) {
        this.shows.clear()
        this.shows.addAll(searchResults)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShowResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.nameText.text = show.name

        Glide.with(holder.itemView.context)
            .load(show.artworkUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener?.onShowSelected(shows[position])
        }
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

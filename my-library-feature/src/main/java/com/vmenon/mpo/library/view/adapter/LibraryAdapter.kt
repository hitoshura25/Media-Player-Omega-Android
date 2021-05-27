package com.vmenon.mpo.library.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vmenon.mpo.library.databinding.LibraryItemBinding
import com.vmenon.mpo.my_library.domain.EpisodeModel

class LibraryAdapter(private val episodes: List<EpisodeModel>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var listener: LibrarySelectedListener? = null

    interface LibrarySelectedListener {
        fun onEpisodeSelected(episodeWithShowDetails: EpisodeModel)
    }

    class ViewHolder(binding: LibraryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.episodeName
        val imageView: ImageView = binding.episodeImage
        var episode: EpisodeModel? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LibraryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vh = ViewHolder(binding)
        binding.root.setOnClickListener {
            vh.episode?.let {
                listener?.onEpisodeSelected(it)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeWithShowDetails = episodes[position]
        holder.episode = episodeWithShowDetails
        holder.nameText.text = episodeWithShowDetails.name

        Glide.with(holder.itemView.context)
            .load(
                episodeWithShowDetails.artworkUrl
                    ?: episodeWithShowDetails.show.artworkUrl
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun setListener(listener: LibrarySelectedListener) {
        this.listener = listener
    }
}

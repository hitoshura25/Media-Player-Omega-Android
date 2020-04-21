package com.vmenon.mpo.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.EpisodeWithShowDetailsModel
import kotlinx.android.synthetic.main.library_item.view.*

class LibraryAdapter(private val episodes: List<EpisodeWithShowDetailsModel>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var listener: LibrarySelectedListener? = null

    interface LibrarySelectedListener {
        fun onEpisodeSelected(episode: EpisodeWithShowDetailsModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val imageView: ImageView = v.episodeImage
        var episode: EpisodeWithShowDetailsModel? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.library_item, parent,
            false
        )
        val vh = ViewHolder(v)
        v.setOnClickListener {
            vh.episode?.let {
                listener?.onEpisodeSelected(it)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episode = episode
        holder.nameText.text = episode.episode.details.episodeName

        Glide.with(holder.itemView.context)
            .load(episode.episode.details.episodeArtworkUrl ?: episode.showDetails.showArtworkUrl)
            .centerCrop()
            .crossFade()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun setListener(listener: LibrarySelectedListener) {
        this.listener = listener
    }
}

package com.vmenon.mpo.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.api.Episode
import kotlinx.android.synthetic.main.library_item.view.*

class LibraryAdapter(private val episodes: List<Episode>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var listener: EpisodesAdapter.EpisodeSelectedListener? = null

    interface LibarySelectedListener {
        fun onEpisodeSelected(episode: Episode)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val imageView: ImageView = v.episodeImage
        var episode: Episode? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.library_item, parent,
            false
        )
        val vh = ViewHolder(v)
        v.setOnClickListener {
            if (listener != null) {
                listener!!.onEpisodeSelected(vh.episode)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episode = episode
        holder.nameText.text = episode.name

        Glide.with(holder.itemView.context)
            .load(episode.artworkUrl)
            .centerCrop()
            .crossFade()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun setListener(listener: EpisodesAdapter.EpisodeSelectedListener) {
        this.listener = listener
    }
}

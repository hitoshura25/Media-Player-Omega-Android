package com.vmenon.mpo.library.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.library.R
import kotlinx.android.synthetic.main.library_item.view.*

class LibraryAdapter(private val episodes: List<EpisodeModel>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var listener: LibrarySelectedListener? = null

    interface LibrarySelectedListener {
        fun onEpisodeSelected(episodeWithShowDetails: EpisodeModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val imageView: ImageView = v.episodeImage
        var episode: EpisodeModel? = null
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
        val episodeWithShowDetails = episodes[position]
        holder.episode = episodeWithShowDetails
        holder.nameText.text = episodeWithShowDetails.name

        Glide.with(holder.itemView.context)
            .load(episodeWithShowDetails.artworkUrl
                ?: episodeWithShowDetails.show.artworkUrl)
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
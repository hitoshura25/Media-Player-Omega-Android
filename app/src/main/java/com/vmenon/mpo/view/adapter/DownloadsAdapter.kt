package com.vmenon.mpo.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.QueuedDownloadModel
import kotlinx.android.synthetic.main.download.view.*
import kotlin.math.roundToLong

class DownloadsAdapter(private val downloads: List<QueuedDownloadModel>) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val progressText: TextView = v.progress
        val imageView: ImageView = v.showImage
        var download: QueuedDownloadModel? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.download, parent,
            false
        )
        val vh = ViewHolder(v)
        v.setOnClickListener { }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val download = downloads[position]
        holder.download = download
        holder.nameText.text = download.episode.episodeName

        var progress: Double =
            if (download.total == 0) 0.0
            else download.progress * 1.0 / download.total

        progress *= 100.0
        holder.progressText.text = "${progress.roundToLong()}%"

        Glide.with(holder.itemView.context)
            .load(download.episode.episodeArtworkUrl ?: download.show.showDetails.showArtworkUrl)
            .centerCrop()
            .crossFade()
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return downloads.size
    }
}

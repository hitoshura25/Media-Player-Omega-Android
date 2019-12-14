package com.vmenon.mpo.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.core.Download
import kotlinx.android.synthetic.main.download.view.*
import kotlin.math.roundToLong

class DownloadsAdapter(private val downloads: List<Download>) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val progressText: TextView = v.progress
        val imageView: ImageView = v.showImage
        var download: Download? = null
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
        holder.nameText.text = download.episode?.name

        var progress = download.progress * 1.0 / download.total
        progress *= 100.0
        holder.progressText.text = "${progress.roundToLong()}%"

        download.episode?.artworkUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .centerCrop()
                .crossFade()
                .into(holder.imageView)
        }

    }

    override fun getItemCount(): Int {
        return downloads.size
    }
}

package com.vmenon.mpo.downloads.presentation.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vmenon.mpo.downloads.presentation.databinding.DownloadBinding
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import kotlin.math.roundToLong

class DownloadsAdapter(private val downloads: List<QueuedDownloadModel>) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    class ViewHolder(binding: DownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.episodeName
        val progressText: TextView = binding.progress
        val imageView: ImageView = binding.showImage
        var download: QueuedDownloadModel? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: DownloadBinding = DownloadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val vh = ViewHolder(binding)
        binding.root.setOnClickListener { }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val download = downloads[position]
        holder.download = download
        holder.nameText.text = download.download.name

        var progress: Double =
            if (download.total == 0) 0.0
            else download.progress * 1.0 / download.total

        progress *= 100.0
        holder.progressText.text = "${progress.roundToLong()}%"

        Glide.with(holder.itemView.context)
            .load(download.download.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return downloads.size
    }
}

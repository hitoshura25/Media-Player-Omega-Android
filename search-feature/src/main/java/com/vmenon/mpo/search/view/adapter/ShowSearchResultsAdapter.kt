package com.vmenon.mpo.search.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil

import com.bumptech.glide.Glide
import com.vmenon.mpo.search.R
import kotlinx.android.synthetic.main.show_result.view.*

class ShowSearchResultsAdapter :
    RecyclerView.Adapter<ShowSearchResultsAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null
    private val shows = ArrayList<com.vmenon.mpo.search.domain.ShowSearchResultModel>()

    interface ShowSelectedListener {
        fun onShowSelected(show: com.vmenon.mpo.search.domain.ShowSearchResultModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.showName
        val imageView: ImageView = v.showImage
    }

    fun setListener(listener: ShowSelectedListener) {
        this.listener = listener
    }

    fun update(searchResults: List<com.vmenon.mpo.search.domain.ShowSearchResultModel>, diffResult: DiffUtil.DiffResult) {
        this.shows.clear()
        this.shows.addAll(searchResults)
        diffResult.dispatchUpdatesTo(this)
    }

    fun clear() {
        this.shows.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.show_result, parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.nameText.text = show.name

        Glide.with(holder.itemView.context)
            .load(show.artworkUrl)
            .centerCrop()
            .crossFade()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener?.onShowSelected(shows[position])
        }
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

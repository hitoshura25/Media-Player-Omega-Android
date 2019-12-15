package com.vmenon.mpo.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.ShowSearchResultsModel
import kotlinx.android.synthetic.main.show_result.view.*

class ShowSearchResultsAdapter(private val shows: List<ShowSearchResultsModel>) :
    RecyclerView.Adapter<ShowSearchResultsAdapter.ViewHolder>() {
    private var listener: ShowSelectedListener? = null

    interface ShowSelectedListener {
        fun onShowSelected(show: ShowSearchResultsModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.showName
        val imageView: ImageView = v.showImage
        var show: ShowSearchResultsModel? = null
    }

    fun setListener(listener: ShowSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.show_result, parent,
            false
        )
        val vh = ViewHolder(v)
        v.setOnClickListener {
            vh.show?.let {
                listener?.onShowSelected(it)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.show = show
        holder.nameText.text = show.show.name

        Glide.with(holder.itemView.context)
            .load(show.show.artworkUrl)
            .centerCrop()
            .crossFade()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return shows.size
    }
}

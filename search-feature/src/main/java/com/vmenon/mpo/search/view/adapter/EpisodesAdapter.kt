package com.vmenon.mpo.search.view.adapter

import androidx.recyclerview.widget.RecyclerView

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView

import com.bumptech.glide.Glide
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.databinding.RecentEpisodeBinding
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import java.text.DateFormat

import java.util.Date

class EpisodesAdapter(
    private val showDetails: ShowSearchResultDetailsModel
) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    private var listener: EpisodeSelectedListener? = null

    interface EpisodeSelectedListener {
        fun onPlayEpisode(episode: ShowSearchResultEpisodeModel)
        fun onDownloadEpisode(episode: ShowSearchResultEpisodeModel)
    }

    class ViewHolder(binding: RecentEpisodeBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.episodeName
        val descriptionText: TextView = binding.episodeDescription
        val publishedText: TextView = binding.episodeDate
        val thumbnailImage: ImageView = binding.episodeImage
        val menuButton: ImageButton = binding.episodeMenuButton
        lateinit var episode: ShowSearchResultEpisodeModel
        lateinit var showDetails: ShowSearchResultModel
    }

    fun setListener(listener: EpisodeSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val vh = ViewHolder(binding)
        vh.menuButton.setOnClickListener {
            val popupMenu = PopupMenu(vh.menuButton.context, vh.menuButton)
            popupMenu.inflate(R.menu.episode_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                if (R.id.download_episode == item.itemId) {
                    listener?.onDownloadEpisode(vh.episode)
                } else if (R.id.play_episode == item.itemId) {
                    listener?.onPlayEpisode(vh.episode)
                }
                false
            }
            popupMenu.show()
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = showDetails.episodes[position]
        holder.episode = episode
        holder.showDetails = showDetails.show
        holder.nameText.text = episode.name
        @Suppress("DEPRECATION")
        holder.descriptionText.text = Html.fromHtml(
            episode.description?.replace("(<(//)img>)|(<img.+?>)".toRegex(), "") ?: ""
        )
        holder.publishedText.text = DateFormat.getDateInstance().format(
            Date(episode.published)
        )

        Glide.with(holder.thumbnailImage.context)
            .load(episode.artworkUrl ?: showDetails.show.artworkUrl)
            .fitCenter()
            .into(holder.thumbnailImage)
        holder.thumbnailImage.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return showDetails.episodes.size
    }
}

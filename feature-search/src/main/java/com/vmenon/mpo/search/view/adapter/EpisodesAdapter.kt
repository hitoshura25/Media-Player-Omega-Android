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
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.R
import kotlinx.android.synthetic.main.recent_episode.view.*
import java.text.DateFormat

import java.util.Date

class EpisodesAdapter(
    private val showDetails: com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    private var listener: EpisodeSelectedListener? = null

    interface EpisodeSelectedListener {
        fun onPlayEpisode(episode: com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel)
        fun onDownloadEpisode(episode: com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val descriptionText: TextView = v.episodeDescription
        val publishedText: TextView = v.episodeDate
        val thumbnailImage: ImageView = v.episodeImage
        val menuButton: ImageButton = v.episodeMenuButton
        lateinit var episode: com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
        lateinit var showDetails: com.vmenon.mpo.search.domain.ShowSearchResultModel
    }

    fun setListener(listener: EpisodeSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.recent_episode, parent,
            false
        )
        val vh = ViewHolder(v)
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

package com.vmenon.mpo.adapter

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
import com.vmenon.mpo.R
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.core.BackgroundService
import kotlinx.android.synthetic.main.recent_episode.view.*
import java.text.DateFormat

import java.text.SimpleDateFormat
import java.util.Date

class EpisodesAdapter(private val show: Show, private val episodes: List<Episode>) :
    RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    private var listener: EpisodeSelectedListener? = null

    interface EpisodeSelectedListener {
        fun onEpisodeSelected(episode: Episode?)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameText: TextView = v.episodeName
        val descriptionText: TextView = v.episodeDescription
        val publishedText: TextView = v.episodeDate
        val thumbnailImage: ImageView = v.episodeImage
        val menuButton: ImageButton = v.episodeMenuButton
        var episode: Episode? = null
        var show: Show? = null
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
        v.setOnClickListener {
            if (listener != null && vh.episode != null) {
                listener!!.onEpisodeSelected(vh.episode)
            }
        }

        vh.menuButton.setOnClickListener {
            val popupMenu = PopupMenu(vh.menuButton.context, vh.menuButton)
            popupMenu.inflate(R.menu.episode_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                if (R.id.download_episode == item.itemId) {
                    BackgroundService.startDownload(
                        vh.menuButton.context,
                        vh.show!!,
                        vh.episode!!
                    )
                }

                false
            }
            popupMenu.show()
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episode = episode
        holder.show = show
        holder.nameText.text = episode.name
        @Suppress("DEPRECATION")
        holder.descriptionText.text = Html.fromHtml(
            episode.description!!.replace("(<(//)img>)|(<img.+?>)".toRegex(), "")
        )
        holder.publishedText.text = DateFormat.getDateInstance().format(
            Date(episode.published)
        )

        if (episode.artworkUrl != null) {
            Glide.with(holder.thumbnailImage.context)
                .load(episode.artworkUrl)
                .fitCenter()
                .into(holder.thumbnailImage)
            holder.thumbnailImage.visibility = View.VISIBLE
        } else {
            holder.thumbnailImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return episodes.size
    }
}

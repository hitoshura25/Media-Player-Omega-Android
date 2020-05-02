package com.vmenon.mpo.api

import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import io.reactivex.Maybe
import io.reactivex.Single

interface MediaPlayerOmegaApi  {
    fun searchPodcasts(keyword: String): Single<List<Show>>
    fun getPodcastDetails(feedUrl: String, maxEpisodes: Int): Single<ShowDetails>
    fun getPodcastUpdate(feedUrl: String, lastEpisodePublishTime: Long): Maybe<Episode>
}

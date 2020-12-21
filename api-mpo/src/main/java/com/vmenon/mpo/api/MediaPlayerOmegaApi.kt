package com.vmenon.mpo.api

import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import io.reactivex.Maybe
import io.reactivex.Single

// TODO: Should not be used anymore if implementing data source implementations in the "framework"
// modules. Remove once refactored
interface MediaPlayerOmegaApi  {
    fun searchPodcasts(keyword: String): Single<List<Show>>
    fun getPodcastDetails(feedUrl: String, maxEpisodes: Int): Single<ShowDetails>
    fun getPodcastUpdate(feedUrl: String, lastEpisodePublishTime: Long): Maybe<Episode>
}

package com.vmenon.mpo.api.retrofit

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import io.reactivex.Maybe
import io.reactivex.Single

class MediaPlayerOmegaRetrofitApi(
    private val service: MediaPlayerOmegaRetrofitService
) : MediaPlayerOmegaApi {
    override fun searchPodcasts(keyword: String): Single<List<Show>> =
        service.searchPodcasts(keyword)

    override fun getPodcastDetails(feedUrl: String, maxEpisodes: Int): Single<ShowDetails> =
        service.getPodcastDetails(feedUrl, maxEpisodes)

    override fun getPodcastUpdate(feedUrl: String, lastEpisodePublishTime: Long): Maybe<Episode> =
        service.getPodcastUpdate(feedUrl, lastEpisodePublishTime)
}
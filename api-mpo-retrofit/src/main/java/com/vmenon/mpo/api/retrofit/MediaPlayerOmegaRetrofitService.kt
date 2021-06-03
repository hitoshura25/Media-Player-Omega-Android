package com.vmenon.mpo.api.retrofit

import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import io.reactivex.Maybe
import io.reactivex.Single

import retrofit2.http.GET
import retrofit2.http.Query

// TODO: Should I Rename API endpoints to not be podcast specific?
interface MediaPlayerOmegaRetrofitService {
    @GET("/podcasts")
    fun searchPodcasts(@Query("keyword") keyword: String): Single<List<Show>>

    @GET("/podcastdetails")
    fun getPodcastDetails(
        @Query("feedUrl") feedUrl: String,
        @Query("maxEpisodes") maxEpisodes: Int
    ): Single<ShowDetails>

    @GET("/podcastupdate")
    fun getPodcastUpdate(
        @Query("feedUrl") feedUrl: String,
        @Query("publishTimestamp") lastEpisodePublishTime: Long
    ): Maybe<Episode>
}

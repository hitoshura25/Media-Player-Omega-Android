package com.vmenon.mpo.service

import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.api.ShowDetails
import io.reactivex.Flowable

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

// TODO: Should I Rename API endpoints to not be podcast specific?
interface MediaPlayerOmegaService {
    @GET("podcasts")
    fun searchPodcasts(@Query("keyword") keyword: String): Observable<List<Show>>

    @GET("podcastdetails")
    fun getPodcastDetails(
        @Query("feedUrl") feedUrl: String,
        @Query("maxEpisodes") maxEpisodes: Int
    ): Flowable<ShowDetails>

    @GET("podcastupdate")
    fun getPodcastUpdate(
        @Query("feedUrl") feedUrl: String,
        @Query("publishTimestamp") lastEpisodePublishTime: Long
    ): Observable<Episode>
}

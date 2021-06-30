package com.vmenon.mpo.common.framework.retrofit

import com.vmenon.mpo.api.model.*
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("/register_user")
    fun registerUser(@Body request: RegisterUserRequest): Single<RegisterUserResponse>

    @GET("/user")
    fun getCurrentUser(): Single<UserDetails>
}

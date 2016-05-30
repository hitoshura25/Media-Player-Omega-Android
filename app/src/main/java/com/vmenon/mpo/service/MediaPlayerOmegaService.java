package com.vmenon.mpo.service;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.api.PodcastDetails;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MediaPlayerOmegaService {
    @GET("podcasts")
    Observable<List<Podcast>> searchPodcasts(@Query("keyword") final String keyword);

    @GET("podcastdetails")
    Observable<PodcastDetails> getPodcastDetails(@Query("feedUrl") final String feedUrl,
                                                 @Query("maxEpisodes") int maxEpisodes);

    @GET("podcastupdate")
    Observable<Episode> getPodcastUpdate(@Query("feedUrl") final String feedUrl,
                                         @Query("publishTimestamp") long lastEpisodePublishTime);
}

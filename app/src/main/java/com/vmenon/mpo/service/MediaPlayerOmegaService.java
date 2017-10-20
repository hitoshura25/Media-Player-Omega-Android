package com.vmenon.mpo.service;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.api.ShowDetails;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

// TODO: Should I Rename API endpoints to not be podcast specific?
public interface MediaPlayerOmegaService {
    @GET("podcasts")
    Observable<List<Show>> searchPodcasts(@Query("keyword") final String keyword);

    @GET("podcastdetails")
    Observable<ShowDetails> getPodcastDetails(@Query("feedUrl") final String feedUrl,
                                              @Query("maxEpisodes") int maxEpisodes);

    @GET("podcastupdate")
    Observable<Episode> getPodcastUpdate(@Query("feedUrl") final String feedUrl,
                                         @Query("publishTimestamp") long lastEpisodePublishTime);
}

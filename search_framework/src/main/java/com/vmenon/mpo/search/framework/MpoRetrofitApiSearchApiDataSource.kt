package com.vmenon.mpo.search.framework

import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.search.data.SearchApiDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MpoRetrofitApiSearchApiDataSource(
    private val retrofitApi: MediaPlayerOmegaRetrofitService,
) : SearchApiDataSource {
    override suspend fun searchShows(searchTerm: String): List<Show> =
        withContext(Dispatchers.IO) {
            retrofitApi.searchPodcasts(searchTerm).await()
        }


    override suspend fun getShowDetails(feedUrl: String, maxEpisodes: Int): ShowDetails =
        withContext(Dispatchers.IO) {
            retrofitApi.getPodcastDetails(feedUrl, maxEpisodes).await()
        }
}
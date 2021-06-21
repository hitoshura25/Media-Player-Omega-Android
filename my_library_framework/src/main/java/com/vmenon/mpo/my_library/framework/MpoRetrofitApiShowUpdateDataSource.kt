package com.vmenon.mpo.my_library.framework

import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MpoRetrofitApiShowUpdateDataSource(
    private val mediaPlayerOmegaRetrofitService: MediaPlayerOmegaRetrofitService
) : ShowUpdateDataSource {
    override suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel? =
        withContext(Dispatchers.IO) {
            mediaPlayerOmegaRetrofitService.getPodcastUpdate(
                show.feedUrl,
                show.lastEpisodePublished
            ).map { episode ->
                ShowUpdateModel(
                    newEpisode = EpisodeModel(
                        name = episode.name,
                        description = episode.description,
                        artworkUrl = episode.artworkUrl,
                        downloadUrl = episode.downloadUrl,
                        lengthInSeconds = episode.length,
                        published = episode.published,
                        type = episode.type,
                        show = show
                    )
                )
            }.blockingGet()
        }
}
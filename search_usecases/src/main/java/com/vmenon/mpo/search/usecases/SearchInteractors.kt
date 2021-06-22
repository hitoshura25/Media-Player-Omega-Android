package com.vmenon.mpo.search.usecases

data class SearchInteractors(
    val searchForShows: SearchForShows,
    val getShowDetails: GetShowDetails,
    val subscribeToShow: SubscribeToShow,
    val queueDownloadForShow: QueueDownloadForShow
)
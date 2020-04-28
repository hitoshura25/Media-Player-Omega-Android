package com.vmenon.mpo.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.model.ShowSearchResultModel

class ShowSearchResultsDiff(
    private val oldSearchResults: List<ShowSearchResultModel>,
    private val newSearchResults: List<ShowSearchResultModel>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSearchResults[oldItemPosition].name ==
                newSearchResults[oldItemPosition].name

    override fun getOldListSize(): Int = oldSearchResults.size

    override fun getNewListSize(): Int = newSearchResults.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSearchResults[oldItemPosition].name == newSearchResults[newItemPosition].name &&
                oldSearchResults[oldItemPosition].artworkUrl == newSearchResults[newItemPosition].artworkUrl &&
                oldSearchResults[oldItemPosition].author == newSearchResults[newItemPosition].author &&
                oldSearchResults[oldItemPosition].description == newSearchResults[newItemPosition].description &&
                oldSearchResults[oldItemPosition].feedUrl == newSearchResults[newItemPosition].feedUrl &&
                oldSearchResults[oldItemPosition].genres == newSearchResults[newItemPosition].genres
}
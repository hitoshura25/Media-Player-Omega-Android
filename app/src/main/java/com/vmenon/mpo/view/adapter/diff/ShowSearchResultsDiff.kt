package com.vmenon.mpo.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.model.ShowSearchResultsModel

class ShowSearchResultsDiff(
    private val oldSearchResults: List<ShowSearchResultsModel>,
    private val newSearchResults: List<ShowSearchResultsModel>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSearchResults[oldItemPosition].showDetails.showName ==
                newSearchResults[oldItemPosition].showDetails.showName

    override fun getOldListSize(): Int = oldSearchResults.size

    override fun getNewListSize(): Int = newSearchResults.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSearchResults[oldItemPosition].showDetails ==
                newSearchResults[newItemPosition].showDetails
}
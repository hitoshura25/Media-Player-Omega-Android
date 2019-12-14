package com.vmenon.mpo.api

import java.util.ArrayList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShowDetails(
        /**
         *
         * @return
         * The name
         */
        /**
         *
         * @param name
         * The name
         */
        @SerializedName("name")
        @Expose
        val name: String? = null,

        /**
         *
         * @return
         * The description
         */
        /**
         *
         * @param description
         * The description
         */
        @SerializedName("description")
        @Expose
        val description: String? = null,

        /**
         *
         * @return
         * The imageUrl
         */
        /**
         *
         * @param imageUrl
         * The imageUrl
         */
        @SerializedName("imageUrl")
        @Expose
        val imageUrl: String? = null,

        /**
         *
         * @return
         * The episodes
         */
        /**
         *
         * @param episodes
         * The episodes
         */
        @SerializedName("episodes")
        @Expose
        var episodes: List<Episode> = ArrayList()
)

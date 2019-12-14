package com.vmenon.mpo.api

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import java.util.ArrayList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.Parcel

@Parcel
@Entity(indices = [Index(value = ["name"], unique = true)])
data class Show(
    @SerializedName("name")
    @Expose
    val name: String = "",

    @SerializedName("artworkUrl")
    @Expose
    val artworkUrl: String? = null,

    @SerializedName("genres")
    @Expose
    val genres: List<String> = ArrayList(),

    @SerializedName("author")
    @Expose
    val author: String? = null,

    @SerializedName("feedUrl")
    @Expose
    val feedUrl: String? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    var lastUpdate: Long = -1L,
    var lastEpisodePublished: Long = -1L
)

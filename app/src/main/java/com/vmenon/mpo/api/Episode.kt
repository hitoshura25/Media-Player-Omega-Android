package com.vmenon.mpo.api

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.Parcel

@Parcel
@Entity(
    foreignKeys = [ForeignKey(
        entity = Show::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("showId")
    )], indices = [Index("showId")]
)
data class Episode(

    @SerializedName("name")
    @Expose
    val name: String = "",

    @SerializedName("description")
    @Expose
    val description: String? = null,

    @SerializedName("published")
    @Expose
    val published: Long = 0,

    @SerializedName("type")
    @Expose
    val type: String? = null,

    @SerializedName("downloadUrl")
    @Expose
    val downloadUrl: String? = null,

    @SerializedName("length")
    @Expose
    val length: Long = 0,

    @SerializedName("artworkUrl")
    @Expose
    var artworkUrl: String? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var showId: Long = 0,

    var filename: String? = null
)
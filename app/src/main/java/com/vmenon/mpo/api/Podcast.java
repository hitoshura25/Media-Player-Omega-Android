package com.vmenon.mpo.api;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity
public class Podcast {

    @PrimaryKey @NonNull
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("artworkUrl")
    @Expose
    public String artworkUrl;

    @SerializedName("genres")
    @Expose
    public List<String> genres = new ArrayList<>();

    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("feedUrl")
    @Expose
    public String feedUrl;

    public long lastUpdate = -1L;
    public long lastEpisodePublished = -1L;
}

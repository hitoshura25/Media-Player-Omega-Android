package com.vmenon.mpo.api;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(indices = {@Index(value = "name", unique = true)})
public class Show {

    @NonNull
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

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long lastUpdate = -1L;
    public long lastEpisodePublished = -1L;
}
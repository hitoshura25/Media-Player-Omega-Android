
package com.vmenon.mpo.api;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Transient;

@Parcel
public class Podcast {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("artworkUrl")
    @Expose
    public String artworkUrl;

    @SerializedName("genres")
    @Expose
    public List<String> genres = new ArrayList<String>();

    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("feedUrl")
    @Expose
    public String feedUrl;

    public long id = -1L;
    public String lastEpisode;
    public long lastUpdate = -1L;
    public long lastEpisodePublished = -1L;
}

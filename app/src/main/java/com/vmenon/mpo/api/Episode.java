
package com.vmenon.mpo.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Episode {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("published")
    @Expose
    public long published;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("downloadUrl")
    @Expose
    public String downloadUrl;

    @SerializedName("length")
    @Expose
    public long length;

    @SerializedName("artworkUrl")
    @Expose
    public String artworkUrl;

    public long id = -1L;
    public long showId;
    public String filename;
}

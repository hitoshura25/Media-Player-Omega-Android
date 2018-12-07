
package com.vmenon.mpo.api;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = Show.class,
        parentColumns = "id",
        childColumns = "showId"),
        indices = @Index("showId"))
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

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long showId;

    public String filename;
}

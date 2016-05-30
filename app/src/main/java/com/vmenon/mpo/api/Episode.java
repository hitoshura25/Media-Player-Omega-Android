
package com.vmenon.mpo.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("published")
    @Expose
    long published;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("downloadUrl")
    @Expose
    String downloadUrl;

    @SerializedName("length")
    @Expose
    long length;

    @SerializedName("artworkUrl")
    @Expose
    String artworkUrl;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The published
     */
    public long getPublished() {
        return published;
    }

    /**
     * 
     * @param published
     *     The published
     */
    public void setPublished(long published) {
        this.published = published;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The downloadUrl
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * 
     * @param downloadUrl
     *     The downloadUrl
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * 
     * @return
     *     The length
     */
    public long getLength() {
        return length;
    }

    /**
     * 
     * @param length
     *     The length
     */
    public void setLength(long length) {
        this.length = length;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }
}

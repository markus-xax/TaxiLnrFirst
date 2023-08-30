package com.example.taxi_full.API.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RootUserGeolocation {

    @SerializedName("Longitude")
    @Expose
    private String longitude;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("DC")
    @Expose
    private String dc;
    @SerializedName("Hash")
    @Expose
    private String hash;
    @SerializedName("Duration")
    @Expose
    private String duration;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
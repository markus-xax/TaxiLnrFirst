package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootTime {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("active")
    @Expose
    private String active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}

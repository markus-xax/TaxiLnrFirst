package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootHomeWork {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hash")
    @Expose
    private String hash = "";
    @SerializedName("home")
    @Expose
    private String home = "";
    @SerializedName("work")
    @Expose
    private String work = "";
    @SerializedName("pointHome")
    @Expose
    private String pointHome = "";
    @SerializedName("pointWork")
    @Expose
    private String pointWork = "";

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getPointHome() {
        return pointHome;
    }

    public void setPointHome(String pointHome) {
        this.pointHome = pointHome;
    }

    public String getPointWork() {
        return pointWork;
    }

    public void setPointWork(String pointWork) {
        this.pointWork = pointWork;
    }

}
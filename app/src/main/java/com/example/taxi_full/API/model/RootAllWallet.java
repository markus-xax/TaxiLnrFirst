package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootAllWallet {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("incoming")
    @Expose
    private String incoming;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("methood")
    @Expose
    private String methood;
    @SerializedName("in_for_take_off")
    @Expose
    private String inForTakeOff;

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

    public String getIncoming() {
        return incoming;
    }

    public void setIncoming(String incoming) {
        this.incoming = incoming;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getMethood() {
        return methood;
    }

    public void setMethood(String methood) {
        this.methood = methood;
    }

    public String getInForTakeOff() {
        return inForTakeOff;
    }

    public void setInForTakeOff(String inForTakeOff) {
        this.inForTakeOff = inForTakeOff;
    }
}

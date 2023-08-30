package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootDebt {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("debt")
    @Expose
    private String debt;
    @SerializedName("date")
    @Expose
    private String date;

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

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

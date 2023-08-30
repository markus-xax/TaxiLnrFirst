package com.example.taxi_full.API.model;

public class RootUserOneGeolocation {
    private double Longitude, Latitude;
    private String DC, Hash;

    public String getDC() {
        return DC;
    }

    public double getLongitude() {
        return Longitude;
    }

    public String getHash() {
        return Hash;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setDC(String DC) {
        this.DC = DC;
    }

    public void setHash(String hash) {
        Hash = hash;
    }
}

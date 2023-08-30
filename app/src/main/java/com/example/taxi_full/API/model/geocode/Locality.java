package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Locality {

   @SerializedName("LocalityName")
   String LocalityName;

   @SerializedName("Thoroughfare")
   Thoroughfare Thoroughfare;


    public void setLocalityName(String LocalityName) {
        this.LocalityName = LocalityName;
    }
    public String getLocalityName() {
        return LocalityName;
    }
    
    public void setThoroughfare(Thoroughfare Thoroughfare) {
        this.Thoroughfare = Thoroughfare;
    }
    public Thoroughfare getThoroughfare() {
        return Thoroughfare;
    }
    
}
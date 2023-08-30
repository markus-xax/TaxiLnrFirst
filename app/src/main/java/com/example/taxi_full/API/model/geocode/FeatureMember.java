package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class FeatureMember {

   @SerializedName("GeoObject")
   GeoObject GeoObject;


    public void setGeoObject(GeoObject GeoObject) {
        this.GeoObject = GeoObject;
    }
    public GeoObject getGeoObject() {
        return GeoObject;
    }
    
}
package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Response {

   @SerializedName("GeoObjectCollection")
   GeoObjectCollection GeoObjectCollection;


    public void setGeoObjectCollection(GeoObjectCollection GeoObjectCollection) {
        this.GeoObjectCollection = GeoObjectCollection;
    }
    public GeoObjectCollection getGeoObjectCollection() {
        return GeoObjectCollection;
    }
    
}
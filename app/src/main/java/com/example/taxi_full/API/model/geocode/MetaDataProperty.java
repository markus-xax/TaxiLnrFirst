package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class MetaDataProperty {

   @SerializedName("GeocoderMetaData")
   GeocoderMetaData GeocoderMetaData;


    public void setGeocoderMetaData(GeocoderMetaData GeocoderMetaData) {
        this.GeocoderMetaData = GeocoderMetaData;
    }
    public GeocoderMetaData getGeocoderMetaData() {
        return GeocoderMetaData;
    }
    
}
package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Envelope {

   @SerializedName("lowerCorner")
   String lowerCorner;

   @SerializedName("upperCorner")
   String upperCorner;


    public void setLowerCorner(String lowerCorner) {
        this.lowerCorner = lowerCorner;
    }
    public String getLowerCorner() {
        return lowerCorner;
    }
    
    public void setUpperCorner(String upperCorner) {
        this.upperCorner = upperCorner;
    }
    public String getUpperCorner() {
        return upperCorner;
    }
    
}
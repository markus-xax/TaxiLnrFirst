package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Point {

   @SerializedName("pos")
   String pos;


    public void setPos(String pos) {
        this.pos = pos;
    }
    public String getPos() {
        return pos;
    }
    
}
package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Thoroughfare {

   @SerializedName("ThoroughfareName")
   String ThoroughfareName;

   @SerializedName("Premise")
   Premise Premise;


    public void setThoroughfareName(String ThoroughfareName) {
        this.ThoroughfareName = ThoroughfareName;
    }
    public String getThoroughfareName() {
        return ThoroughfareName;
    }
    
    public void setPremise(Premise Premise) {
        this.Premise = Premise;
    }
    public Premise getPremise() {
        return Premise;
    }
    
}
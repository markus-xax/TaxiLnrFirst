package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Premise {

   @SerializedName("PremiseNumber")
   String PremiseNumber;


    public void setPremiseNumber(String PremiseNumber) {
        this.PremiseNumber = PremiseNumber;
    }
    public String getPremiseNumber() {
        return PremiseNumber;
    }
    
}
package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class SubAdministrativeArea {

   @SerializedName("SubAdministrativeAreaName")
   String SubAdministrativeAreaName;

   @SerializedName("Locality")
   Locality Locality;


    public void setSubAdministrativeAreaName(String SubAdministrativeAreaName) {
        this.SubAdministrativeAreaName = SubAdministrativeAreaName;
    }
    public String getSubAdministrativeAreaName() {
        return SubAdministrativeAreaName;
    }
    
    public void setLocality(Locality Locality) {
        this.Locality = Locality;
    }
    public Locality getLocality() {
        return Locality;
    }
    
}
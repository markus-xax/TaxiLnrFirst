package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class AdministrativeArea {

   @SerializedName("AdministrativeAreaName")
   String AdministrativeAreaName;

   @SerializedName("SubAdministrativeArea")
   SubAdministrativeArea SubAdministrativeArea;


    public void setAdministrativeAreaName(String AdministrativeAreaName) {
        this.AdministrativeAreaName = AdministrativeAreaName;
    }
    public String getAdministrativeAreaName() {
        return AdministrativeAreaName;
    }
    
    public void setSubAdministrativeArea(SubAdministrativeArea SubAdministrativeArea) {
        this.SubAdministrativeArea = SubAdministrativeArea;
    }
    public SubAdministrativeArea getSubAdministrativeArea() {
        return SubAdministrativeArea;
    }
    
}
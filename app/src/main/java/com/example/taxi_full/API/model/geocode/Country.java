package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class Country {

   @SerializedName("AddressLine")
   String AddressLine;

   @SerializedName("CountryNameCode")
   String CountryNameCode;

   @SerializedName("CountryName")
   String CountryName;

   @SerializedName("AdministrativeArea")
   AdministrativeArea AdministrativeArea;


    public void setAddressLine(String AddressLine) {
        this.AddressLine = AddressLine;
    }
    public String getAddressLine() {
        return AddressLine;
    }
    
    public void setCountryNameCode(String CountryNameCode) {
        this.CountryNameCode = CountryNameCode;
    }
    public String getCountryNameCode() {
        return CountryNameCode;
    }
    
    public void setCountryName(String CountryName) {
        this.CountryName = CountryName;
    }
    public String getCountryName() {
        return CountryName;
    }
    
    public void setAdministrativeArea(AdministrativeArea AdministrativeArea) {
        this.AdministrativeArea = AdministrativeArea;
    }
    public AdministrativeArea getAdministrativeArea() {
        return AdministrativeArea;
    }
    
}
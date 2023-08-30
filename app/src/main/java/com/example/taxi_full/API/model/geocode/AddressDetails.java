package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class AddressDetails {

   @SerializedName("Country")
   Country Country;


    public void setCountry(Country Country) {
        this.Country = Country;
    }
    public Country getCountry() {
        return Country;
    }
    
}
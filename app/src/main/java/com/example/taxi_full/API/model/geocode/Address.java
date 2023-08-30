package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

   
public class Address {

   @SerializedName("country_code")
   String countryCode;

   @SerializedName("formatted")
   String formatted;

   @SerializedName("Components")
   List<Components> Components;


    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }
    public String getFormatted() {
        return formatted;
    }
    
    public void setComponents(List<Components> Components) {
        this.Components = Components;
    }
    public List<Components> getComponents() {
        return Components;
    }
    
}
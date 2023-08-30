package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class GeocoderMetaData {

   @SerializedName("precision")
   String precision;

   @SerializedName("text")
   String text;

   @SerializedName("kind")
   String kind;

   @SerializedName("Address")
   Address Address;

   @SerializedName("AddressDetails")
   AddressDetails AddressDetails;


    public void setPrecision(String precision) {
        this.precision = precision;
    }
    public String getPrecision() {
        return precision;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getKind() {
        return kind;
    }
    
    public void setAddress(Address Address) {
        this.Address = Address;
    }
    public Address getAddress() {
        return Address;
    }
    
    public void setAddressDetails(AddressDetails AddressDetails) {
        this.AddressDetails = AddressDetails;
    }
    public AddressDetails getAddressDetails() {
        return AddressDetails;
    }
    
}
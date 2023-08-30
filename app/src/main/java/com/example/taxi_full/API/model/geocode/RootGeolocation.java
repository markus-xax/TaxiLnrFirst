package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class RootGeolocation {

   @SerializedName("response")
   Response response;


    public void setResponse(Response response) {
        this.response = response;
    }
    public Response getResponse() {
        return response;
    }
    
}
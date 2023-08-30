package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class BoundedBy {

   @SerializedName("Envelope")
   Envelope Envelope;


    public void setEnvelope(Envelope Envelope) {
        this.Envelope = Envelope;
    }
    public Envelope getEnvelope() {
        return Envelope;
    }
    
}
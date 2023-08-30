package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class GeocoderResponseMetaData {

   @SerializedName("Point")
   Point Point;

   @SerializedName("boundedBy")
   BoundedBy boundedBy;

   @SerializedName("request")
   String request;

   @SerializedName("results")
   String results;

   @SerializedName("found")
   String found;


    public void setPoint(Point Point) {
        this.Point = Point;
    }
    public Point getPoint() {
        return Point;
    }
    
    public void setBoundedBy(BoundedBy boundedBy) {
        this.boundedBy = boundedBy;
    }
    public BoundedBy getBoundedBy() {
        return boundedBy;
    }
    
    public void setRequest(String request) {
        this.request = request;
    }
    public String getRequest() {
        return request;
    }
    
    public void setResults(String results) {
        this.results = results;
    }
    public String getResults() {
        return results;
    }
    
    public void setFound(String found) {
        this.found = found;
    }
    public String getFound() {
        return found;
    }
    
}
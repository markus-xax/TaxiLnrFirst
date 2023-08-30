package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

   
public class GeoObject {

   @SerializedName("metaDataProperty")
   MetaDataProperty metaDataProperty;

   @SerializedName("name")
   String name;

   @SerializedName("description")
   String description;

   @SerializedName("boundedBy")
   BoundedBy boundedBy;

   @SerializedName("Point")
   Point Point;


    public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
        this.metaDataProperty = metaDataProperty;
    }
    public MetaDataProperty getMetaDataProperty() {
        return metaDataProperty;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setBoundedBy(BoundedBy boundedBy) {
        this.boundedBy = boundedBy;
    }
    public BoundedBy getBoundedBy() {
        return boundedBy;
    }
    
    public void setPoint(Point Point) {
        this.Point = Point;
    }
    public Point getPoint() {
        return Point;
    }
    
}
package com.example.taxi_full.API.model.geocode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

   
public class GeoObjectCollection {

   @SerializedName("metaDataProperty")
   MetaDataProperty metaDataProperty;

   @SerializedName("featureMember")
   List<FeatureMember> featureMember;


    public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
        this.metaDataProperty = metaDataProperty;
    }
    public MetaDataProperty getMetaDataProperty() {
        return metaDataProperty;
    }
    
    public void setFeatureMember(List<FeatureMember> featureMember) {
        this.featureMember = featureMember;
    }
    public List<FeatureMember> getFeatureMember() {
        return featureMember;
    }
    
}
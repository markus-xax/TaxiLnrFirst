package com.example.taxi_full.API.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Current {

    @SerializedName("temp_c")
    @Expose
    private Double tempC;
    @SerializedName("is_day")
    @Expose
    private Integer isDay;
    @SerializedName("condition")
    @Expose
    private Condition condition;
    @SerializedName("precip_mm")
    @Expose
    private Double precipMm;

    public Double getTempC() {
        return tempC;
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }

    public Integer getIsDay() {
        return isDay;
    }

    public void setIsDay(Integer isDay) {
        this.isDay = isDay;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Double getPrecipMm() {
        return precipMm;
    }

    public void setPrecipMm(Double precipMm) {
        this.precipMm = precipMm;
    }

}
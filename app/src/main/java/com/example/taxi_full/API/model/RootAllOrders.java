package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RootAllOrders {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hash_user")
    @Expose
    private String hashUser;
    @SerializedName("hash_driver")
    @Expose
    private String hashDriver;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("finish")
    @Expose
    private String finish;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("name_user")
    @Expose
    private String nameUser;
    @SerializedName("name_driver")
    @Expose
    private String nameDriver;
    @SerializedName("start_string")
    @Expose
    private String start_string;
    @SerializedName("finish_string")
    @Expose
    private String finish_string;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("class")
    @Expose
    private String classOrder;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type_pay")
    @Expose
    private String type_pay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHashUser() {
        return hashUser;
    }

    public void setHashUser(String hashUser) {
        this.hashUser = hashUser;
    }

    public String getHashDriver() {
        return hashDriver;
    }

    public void setHashDriver(String hashDriver) {
        this.hashDriver = hashDriver;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNameDriver() {
        return nameDriver;
    }

    public void setNameDriver(String nameDriver) {
        this.nameDriver = nameDriver;
    }

    public String getStart_string() {
        return start_string;
    }

    public String getFinish_string() {
        return finish_string;
    }

    public void setStart_string(String start_string) {
        this.start_string = start_string;
    }

    public void setFinish_string(String finish_string) {
        this.finish_string = finish_string;
    }

    public String getPrice() {
        return price;
    }

    public String getDistance() {
        return distance;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getClassOrder() {
        return classOrder;
    }

    public void setClassOrder(String classOrder) {
        this.classOrder = classOrder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType_pay() {
        return type_pay;
    }

    public void setType_pay(String type_pay) {
        this.type_pay = type_pay;
    }
}

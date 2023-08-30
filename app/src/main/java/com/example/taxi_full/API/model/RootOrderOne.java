package com.example.taxi_full.API.model;

public class RootOrderOne {
    private String id, hash_user, hash_driver, start, finish, active,nameUser, nameDriver, error, start_string, finish_string, price, distance, type_pay;

    public String getNameDriver() {
        return nameDriver;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameDriver(String nameDriver) {
        this.nameDriver = nameDriver;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getId() {
        return id;
    }

    public String getHash_user() {
        return hash_user;
    }

    public String getHash_driver() {
        return hash_driver;
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }

    public String getActive() {
        return active;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHash_user(String hash_user) {
        this.hash_user = hash_user;
    }

    public void setHash_driver(String hash_driver) {
        this.hash_driver = hash_driver;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setFinish_string(String finish_string) {
        this.finish_string = finish_string;
    }

    public void setStart_string(String start_string) {
        this.start_string = start_string;
    }

    public String getFinish_string() {
        return finish_string;
    }

    public String getStart_string() {
        return start_string;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType_pay() {
        return type_pay;
    }

    public void setType_pay(String type_pay) {
        this.type_pay = type_pay;
    }
}

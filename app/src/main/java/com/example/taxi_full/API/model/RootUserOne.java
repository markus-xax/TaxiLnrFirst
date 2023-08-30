package com.example.taxi_full.API.model;

public class RootUserOne {


    private String id, name, surname, email, phone, hash, sms, activeSms, rate = "";
    private String error = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getActiveSms() {
        return activeSms;
    }

    public void setActiveSms(String activeSms) {
        this.activeSms = activeSms;
    }

    public String getNameSurname(){
        return this.name+" "+this.surname;
    }

    public String getPhoneTrim(){
        return "+"+phone.trim();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
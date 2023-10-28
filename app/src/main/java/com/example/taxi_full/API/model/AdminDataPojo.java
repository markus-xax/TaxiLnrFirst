package com.example.taxi_full.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

    public class AdminDataPojo {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("commission")
        @Expose
        private String commission;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("login")
        @Expose
        private String login;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }


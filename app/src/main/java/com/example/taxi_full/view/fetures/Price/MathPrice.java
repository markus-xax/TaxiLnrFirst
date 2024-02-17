package com.example.taxi_full.view.fetures.Price;

public class MathPrice implements Price {
    @Override
    public int mathPrice(int mathPrice, String weather, String delivery, String day) {
        return  Math.round(mathPrice + (mathPrice * (Integer.parseInt(weather) + Integer.parseInt(delivery) + Integer.parseInt(day)) / 100));
    }

}

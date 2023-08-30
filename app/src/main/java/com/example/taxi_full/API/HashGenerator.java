package com.example.taxi_full.API;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class HashGenerator {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String hash() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }



}

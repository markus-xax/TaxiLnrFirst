package com.example.taxi_full.API;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regx {

    public static boolean EmailRegx(String email){
        String regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcher = patternEmail.matcher(email);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    public static boolean PhoneRegx(String phone){
        String regexPhone = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
        Pattern patternPhone = Pattern.compile(regexPhone);
        Matcher matcherPhone = patternPhone.matcher(phone);
        if(matcherPhone.matches()){
            return true;
        }
        return false;
    }
}

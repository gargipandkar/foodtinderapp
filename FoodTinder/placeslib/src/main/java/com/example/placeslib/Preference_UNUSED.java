package com.example.placeslib;

public class Preference_UNUSED {
    private String location;
    private String money;
    Preference_UNUSED(String location, String money){
        this.location = location;
        this.money = money;
    }
    Preference_UNUSED(String money){
        this.location = null;
        this.money = money;
    }

    public String getLocation() {
        return location;
    }

    public String getMoney() {
        return money;
    }
}

package com.example.foodtinder;

public class Preference {
    private String location;
    private String money;
    Preference(String location,String money){
        this.location = location;
        this.money = money;
    }
    Preference(String money){
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

package com.example.foodtinder;

//NearbySearch implementation of Places API
//Nearby Search require location (longitude and latitude), radius and api_key as compulsory parameter
//It may also receive minprice and maxprice (value ranging from 0, the cheapest, to 4, most expensive)
//Returns at most 20 places fulfilling the parameter passed, including their place_id, which may be passed to Place Details

public class NearbySearch {

    private String format = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private double longitude, latitude;
    private int radius, minprice, maxprice;
    private String type;

    //constructor method
    NearbySearch(double longitude, double latitude, int radius, int minprice, int maxprice, String type){
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.type = type;
    }

    //generate url to be send to Response Class
    String generateURL(){
        String query = String.valueOf(this.latitude) + "," + String.valueOf(this.longitude)+"&radius=" + String.valueOf(this.radius) + "&minprice=" + String.valueOf(this.minprice) + "&maxprice=" + String.valueOf(this.maxprice) + "&type=" + this.type;
        return format + query + "&key=";
    }
}

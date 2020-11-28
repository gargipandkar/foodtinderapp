package com.example.foodtinder;
import java.util.ArrayList;

//PlaceDetails implementation of Places API
//Place Details require place_id and api_key as parameter
//Returns the complete attributes of the place specified by place_id

public class PlaceDetails {
    ArrayList<String> placeIDList;
    String format = "https://maps.googleapis.com/maps/api/place/details/json?";
    ArrayList<String> urlList = new ArrayList<>();

    //constructor method
    PlaceDetails(ArrayList<String> placeIDs){
        placeIDList = placeIDs;
    }

    //generate url to be used by Response class
    void generateURL(){
        for (String placeID:placeIDList){
            String query = "place_id="+ placeID;
            urlList.add(format + query + "&key=");
        }
    }


}

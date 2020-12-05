package com.example.foodtinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class URLGenerator {

    private String API_KEY = "AIzaSyD72OjefU10z49J3qCD6Cs2iXHi8uvVBjo";
    String inputType = "json";

    String generateFindPlaceURL(String input, String input2, String input3){
        String format = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
        ArrayList<String> fields = new ArrayList<>(Arrays.asList("name","formatted_address","types","photos","place_id","geometry"));
        String noSpaceInput = input.replaceAll("\\s+","+");
        String query="";
        for (String field:fields){
            query+=field+",";
        }
        query = query.substring(0,query.length()-1);
        return format + "input=" + noSpaceInput + "&inputtype=" + "textquery" + "&fields=" + query + "&key=" + API_KEY;
    }

    String generateNearbySearchURL(HashMap<String, String> queryInfo){
        String format = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String query = String.valueOf(queryInfo.get("latitude")) + "," + String.valueOf(queryInfo.get("longitude"))+ "&minprice=" + String.valueOf(queryInfo.get("min_price")) + "&maxprice=" + String.valueOf(queryInfo.get("max_price")) + "&radius=1000" + "&type=" + "restaurant";
        return format + query + "&key=" + API_KEY;
    }

    String generatePlaceDetailsURL(String placeID){
        String format = "https://maps.googleapis.com/maps/api/place/details/json?";
        return format + "place_id="+ placeID + "&key=" + API_KEY;
    }

    String generatePlacesPhotosURL(String reference){
        String format = "https://maps.googleapis.com/maps/api/place/photo?";
        String query = "maxwidth=1600" + "&maxheight=1600" + "&photoreference="+reference;
        return format + query + "&key=" + API_KEY;
    }
}
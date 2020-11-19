package com.example.lib;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//Result class to keep track of results from Places API search requests
//Current implementation include Places API request for Nearby Search and Place Details

public class Result {
    ArrayList<String> placeIDs = new ArrayList<>();
    HashMap<String,HashMap<String,Object>> placeDetails = new HashMap<String, HashMap<String,Object>>();
    HashMap<String,Object> placeInfo = new HashMap<>();

    void addPlaceIDs(String id){
        this.placeIDs.add(id);
    }

    //parser to process result from NearbySearch requests
    void nearbySearchParser(String responseBody){
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        JSONArray resultsArr = resultsObj.getJSONArray("results");
        for (int i=0;i<resultsArr.length();i++) {
            JSONObject resultFind = resultsArr.getJSONObject(i);
            this.addPlaceIDs(resultFind.getString("place_id"));
        }
    }

    //parser to process result from PlaceDetails requests
    void placeDetailsParser(String responseBody){
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        org.json.JSONObject resultsArr = resultsObj.getJSONObject("result");

        HashMap<String,Object> attributes = this.placeDetails.get(resultsArr.getString("name"));
        if (attributes==null) {
            attributes = new HashMap<String, Object>();
        }

        //add each desired attributes to the HashMap, add or remove according to need and specifications of application
        attributes.put("formatted_address",resultsArr.getString("formatted_address"));
        attributes.put("business_status",resultsArr.getString("business_status"));
        attributes.put("price_level",resultsArr.getInt("price_level"));
        attributes.put("ratings",resultsArr.getInt("price_level"));
        if (resultsArr.has("photos")){
            attributes.put("photos",resultsArr.get("photos"));
        } else {
            attributes.put("photos",null);
        }
        this.placeDetails.put(resultsArr.getString("name"),attributes);
    }

    //parser to process result from findPlace requests
    void findPlaceParser(String responseBody){
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        JSONArray resultsArr = resultsObj.getJSONArray("candidates");
        JSONObject finResult = resultsArr.getJSONObject(0);
        this.placeInfo.put("longitude",finResult.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
        this.placeInfo.put("latitude",finResult.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
        this.placeInfo.put("name",finResult.getString("name"));
        this.placeInfo.put("formatted_address",finResult.getString("formatted_address"));
        this.placeInfo.put("types",finResult.getJSONArray("types"));
        this.placeInfo.put("place_id",finResult.getString("place_id"));
        this.placeInfo.put("photos",finResult.getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
    }
}

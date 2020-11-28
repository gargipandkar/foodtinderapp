package com.example.foodtinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//Result class to keep track of results from Places API search requests
//Current implementation include Places API request for Nearby Search and Place Details

public class Result {
    ArrayList<String> placeIDs = new ArrayList<>();
    HashMap<String,HashMap<String,String>> placeDetails = new HashMap<String, HashMap<String,String>>();
    HashMap<String,HashMap<String,Object>> placeDetailsPhotos = new HashMap<String, HashMap<String,Object>>();
    HashMap<String,Object> placeInfo = new HashMap<>();

    void addPlaceIDs(String id){
        this.placeIDs.add(id);
    }

    //parser to process result from NearbySearch requests
    void nearbySearchParser(String responseBody) throws JSONException {
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        JSONArray resultsArr = resultsObj.getJSONArray("results");
        for (int i=0;i<resultsArr.length();i++) {
            JSONObject resultFind = resultsArr.getJSONObject(i);
            this.addPlaceIDs(resultFind.getString("place_id"));
        }
    }

    //parser to process result from PlaceDetails requests
    void placeDetailsParser(String responseBody) throws JSONException {
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        org.json.JSONObject resultsArr = resultsObj.getJSONObject("result");
        HashMap<String,String> attributes = this.placeDetails.get(resultsArr.getString("name"));
        if (attributes==null) {
            attributes = new HashMap<String, String>();
        }

        String[] keys = new String[]{"formatted_address","business_status","price_level","rating"};

        for(String key:keys){
            if (resultsArr.has(key)==true){
                if (key=="price_level" | key=="rating"){
                    attributes.put(key,Integer.toString(resultsArr.getInt(key)));
                }else {
                    attributes.put(key, resultsArr.getString(key));
                }
            }
            else{
                attributes.put(key,null);
            }

        }
        this.placeDetails.put(resultsArr.getString("name"),attributes);

        HashMap<String,Object> attributes2 = this.placeDetailsPhotos.get(resultsArr.getString("name"));
        if (attributes2==null) {
            attributes2 = new HashMap<String, Object>();
        }
        if (resultsArr.has("photos")){
            attributes2.put("photos", resultsArr.getJSONArray("photos"));
        }
        else {
            attributes2.put("photos", null);
        }

        this.placeDetailsPhotos.put(resultsArr.getString("name"),attributes2);
    }


    //parser to process result from findPlace requests
    void findPlaceParser(String responseBody) throws JSONException {
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

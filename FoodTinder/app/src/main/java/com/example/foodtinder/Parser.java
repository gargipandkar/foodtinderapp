package com.example.foodtinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    HashMap<String,String> queryInfo = new HashMap<>();
    ArrayList<String> placeIDs = new ArrayList<>();
    HashMap<String,HashMap<String,String>> placeDetails = new HashMap<String, HashMap<String,String>>();
    HashMap<String,ArrayList<String>> placeDetailsPhotos = new HashMap<String, ArrayList<String>>();
    HashMap<String,ArrayList<String>> placeDetailsReviews = new HashMap<String,ArrayList<String>>();
    String eventId;

    private static Parser instance = null;

    private Parser(){}

    public static Parser getParser(){
        if (instance==null){
            instance = new Parser();
        }
        return instance;

    }

    public void clear(){
        this.placeDetails.clear();
        this.placeDetailsPhotos.clear();
        this.placeDetailsReviews.clear();
        this.placeIDs.clear();
        this.queryInfo.clear();
    }

    void setEventId(String eventId){
        this.eventId = eventId;
    }

    void findPlaceParser(String toParse) throws JSONException {
        JSONArray result = new JSONArray(toParse);
        JSONObject resultObj = result.getJSONObject(0);
        JSONArray resultsArr = resultObj.getJSONArray("candidates");
        JSONObject finResult = resultsArr.getJSONObject(0);
        this.queryInfo.put("longitude",finResult.getJSONObject("geometry").getJSONObject("location").getString("lng"));
        this.queryInfo.put("latitude",finResult.getJSONObject("geometry").getJSONObject("location").getString("lat"));
        this.queryInfo.put("place_id",finResult.getString("place_id"));

    }


    void addPlaceIDs(String id){
        this.placeIDs.add(id);
    }

    void nearbySearchParser(String responseBody) throws JSONException {
        JSONArray results = new JSONArray(responseBody);
        JSONObject resultsObj =  results.getJSONObject(0);
        JSONArray resultsArr = resultsObj.getJSONArray("results");
        for (int i=0;i<resultsArr.length();i++) {
            JSONObject resultFind = resultsArr.getJSONObject(i);
            this.addPlaceIDs(resultFind.getString("place_id"));
        }
    }

    void placeDetailsParser(String toParse) throws JSONException{
        JSONArray results = new JSONArray(toParse);
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
        this.placeDetails.put(resultsArr.getString("name").replaceAll("[/.#$]"," "),attributes);

        ArrayList<String> attributes2 = this.placeDetailsPhotos.get(resultsArr.get("name"));
        if (attributes2==null) {
            attributes2 = new ArrayList<String>();
        }
        else{
            attributes2.add(null);
        }
        if (resultsArr.has("photos")){
            URLGenerator urlGenerator = new URLGenerator();
            for (int i=0;i<(int)resultsArr.getJSONArray("photos").length();i++){
                String photoRef = resultsArr.getJSONArray("photos").getJSONObject(i).getString("photo_reference");
                attributes2.add(urlGenerator.generatePlacesPhotosURL(photoRef));
            }
        }

        this.placeDetailsPhotos.put(resultsArr.getString("name").replaceAll("[/.#$]"," "),attributes2);

        ArrayList<String> attributes3 = this.placeDetailsReviews.get(resultsArr.getString("name"));
        if (attributes3==null) {
            attributes3 = new ArrayList<String>();
        }
        if (resultsArr.has("reviews")){
            for (int i=0; i<(int)resultsArr.getJSONArray("reviews").length();i++){
                attributes3.add(resultsArr.getJSONArray("reviews").getJSONObject(i).getString("text"));
            }
        }
        else {
            attributes3.add(null);
        }
        this.placeDetailsReviews.put(resultsArr.getString("name").replaceAll("[/.#$]"," "),attributes3);

    }

}